import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class AlgoritmeGenetika {

    int popsize;
    int iterasi;
    double cr, mr;
    int gen;
    double[][] bil;
    double[][] akurasi;
    int[] kelasHasil;

    ELM elm;

    public AlgoritmeGenetika(int popsize, int iterasi, double cr, double mr,
            int hiddenLayer, File file, int jumlahKelas) throws Exception {
        this.cr = cr;
        this.mr = mr;
        this.popsize = popsize;
        elm = new ELM(hiddenLayer, file, jumlahKelas);
        gen = hiddenLayer * elm.dataLatih[0].length;
        kelasHasil = new int[elm.dataUji.length];
        bil = new double[popsize][gen];
        bil = this.inisialisasi();
        for (int i = 0; i < iterasi; i++) {
            //this.show(bil,"Individu Awal");
            bil = this.crossover(bil);
            bil = this.mutasi(bil);
            akurasi = accuracy(bil);
            bil = this.seleksi(akurasi);
            //System.out.println(elm.akurasi(elm.prediksi(bil[0])));
        }
        //this.show(akurasi, "Konvergensi");
        elm.showHasil(bil[0]);
    }

    void show(double[][] data, String name) {
        System.out.println(name);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                System.out.printf("%.3f%s", data[i][j], "\t");
            }
            System.out.println("");
        }
    }

    double[][] inisialisasi() {
        Random rand = new Random();
        double max = 1, min = -1;
        for (int i = 0; i < this.popsize; i++) {
            for (int j = 0; j < gen; j++) {
                bil[i][j] = (rand.nextDouble() * ((max - min))) + min;
            }
        }
        return bil;
    }

    double[][] crossover(double[][] bil) {
        double[] alpha = new double[gen];
        int jumlahParent;
        int jumlahChild = (int) Math.ceil(cr * popsize);
        double child[][] = new double[jumlahChild][gen];
        double tempIndividu[][] = new double[bil.length + jumlahChild][gen];
        int counterCrossover = bil.length;

        if (jumlahChild % 2 == 0) {
            jumlahParent = jumlahChild;
        } else {
            jumlahParent = jumlahChild + 1;
        }

        double parentTerpilih[][] = new double[jumlahParent][gen];
        int min = 0, max = popsize;

        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i = min; i < max; i++) {
            list.add(new Integer(i));
        }
        Collections.shuffle(list);
        for (int i = 0; i < jumlahParent; i++) {
            //System.out.println("Parent " + list.get(i));
            for (int j = 0; j < gen; j++) {
                if (j == (gen - 1)) {
                    parentTerpilih[i][j] = bil[list.get(i)][j];
                } else {
                    parentTerpilih[i][j] = bil[list.get(i)][j];
                }
            }
        }
        //individu gabungan awal
        for (int i = 0; i < bil.length; i++) {
            for (int j = 0; j < gen; j++) {
                tempIndividu[i][j] = bil[i][j];
            }
        }

        for (int i = 0; i < gen; i++) {
            Random rand = new Random();
            double minAlpha = -0.25, maxAlpha = 1.25;
            alpha[i] = (rand.nextDouble() * (maxAlpha - minAlpha)) + minAlpha;
        }

        for (int i = 0; i < jumlahChild; i++) {
            for (int j = 0; j < gen; j++) {
                if (i == 0 || i % 2 == 0) {
                    child[i][j] = parentTerpilih[i][j] + (alpha[j] * (parentTerpilih[i + 1][j] - parentTerpilih[i][j]));
                    tempIndividu[counterCrossover][j] = child[i][j];
                } else {
                    child[i][j] = parentTerpilih[i][j] + (alpha[j] * (parentTerpilih[i - 1][j] - parentTerpilih[i][j]));
                    tempIndividu[counterCrossover][j] = child[i][j];
                }
            }
            counterCrossover++;
        }
        return tempIndividu;
    }

    double[][] mutasi(double[][] x) {
        int index = (int) (Math.random() * gen);
        int jumlahMutasi = (int) (Math.ceil(mr * popsize));
        double childMutasi[][] = new double[jumlahMutasi][gen];
        double tempIndividu[][] = new double[x.length + jumlahMutasi][gen];
        int counterMutasi = x.length;

        for (int i = 0; i < x.length; i++) {
            for (int j = 0; j < gen; j++) {
                tempIndividu[i][j] = x[i][j];
            }
        }

        for (int i = 0; i < jumlahMutasi; i++) {
            Random rand = new Random();
            double minR = -0.1, maxR = 0.1;
            double r = (rand.nextDouble() * (maxR - minR)) + minR;
            int parent = (int) (0 + Math.random() * popsize);
            for (int j = 0; j < gen; j++) {
                childMutasi[i][j] = bil[parent][j];
                if (j == index) {
                    bil[parent][index] = bil[parent][index] + (r * (1 - (-1)));
                    childMutasi[i][j] = bil[parent][j];
                }
                tempIndividu[counterMutasi][j] = childMutasi[i][j];
            }
            counterMutasi++;
        }
        return tempIndividu;
    }

    double[][] seleksi(double[][] fitness) {
        double temp[] = new double[fitness.length];
        for (int i = 0; i < fitness.length; i++) {
            for (int j = i + 1; j < fitness.length; j++) {
                if (fitness[i][gen] < fitness[j][gen]) {
                    temp = fitness[i];
                    fitness[i] = fitness[j];
                    fitness[j] = temp;
                }
            }
        }

        //yang dipilih
        double[][] individuSeleksi = new double[popsize][gen];
        for (int i = 0; i < popsize; i++) {
            for (int j = 0; j < gen; j++) {
                individuSeleksi[i][j] = fitness[i][j];
            }
        }
        return fitness;
    }

    public double[][] accuracy(double[][] data) {
        double[][] temp = new double[data.length][data[0].length + 1];
        double fitness = 0;
        int[] kelasPrediksi = new int[elm.dataUji.length];
        for (int i = 0; i < temp.length; i++) {
            kelasPrediksi = elm.prediksi(data[i]);
            fitness = elm.akurasi(kelasPrediksi);
            for (int j = 0; j < temp[0].length; j++) {
                if (j < temp[0].length - 1) {
                    temp[i][j] = data[i][j];
                } else {
                    temp[i][j] = fitness;
                }
            }
        }
        return temp;
    }
}
