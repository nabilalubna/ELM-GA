package GiziRapih;

import java.io.File;

public class ELM {

    double[][] allData;
    double[][] dataLatih;
    double[][] dataUji;
    int[] kelasDataLatih;
    int[] kelasDataUji;
    int hiddenLayer;
    double[][] dataNormalisasi;
    double[][] dataUjiNorm;
    int jumlahKelas;
    double[][] hInit, h, moorePenrose, hInitTest, hTest, y;
    double[][] bobot;
    double[][] beta;

    BacaFileExcel data;

    public ELM(int hiddenLayer, File file, int jumlahKelas) throws Exception {
        this.hiddenLayer = hiddenLayer;
        data = new BacaFileExcel(file);
        allData = data.getData(2);
        allData = data.getData(2);
        dataLatih = data.getData(3);
        kelasDataLatih = data.getKelasData(3);
        dataUji = data.getData(4);
        kelasDataUji = data.getKelasData(4);
        this.jumlahKelas = jumlahKelas;
        dataNormalisasi = normalisasi(allData, dataLatih);
        dataUjiNorm = normalisasi(allData, dataUji);
//        this.show(dataUji, "DATA UJI");
//        this.show(dataLatih, "DATA LATIH");
    }

    double[] minValue(double x[][]) {
        double minVal;
        double minValArray[] = new double[x[0].length];
        for (int col = 0; col < x[0].length; col++) {
            minVal = 99999;
            for (int row = 0; row < x.length; row++) {
                if (x[row][col] < minVal) {
                    minVal = x[row][col];
                }
            }
            minValArray[col] = minVal;
        }
        return minValArray;
    }

    double[] maxValue(double x[][]) {
        double maxVal;
        double maxValArray[] = new double[x[0].length];
        for (int col = 0; col < x[0].length; col++) {
            maxVal = -99999;
            for (int row = 0; row < x.length; row++) {
                if (x[row][col] > maxVal) {
                    maxVal = x[row][col];
                }
            }
            maxValArray[col] = maxVal;
        }
        return maxValArray;
    }

    double[][] normalisasi(double x[][], double data[][]) {
        double max[] = maxValue(x);
        double min[] = minValue(x);
        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[0].length; col++) {
                data[row][col] = Math.abs(data[row][col] - (min[col])) / Math.abs((max[col]) - (min[col]));
                data[row][col] = data[row][col];
            }
        }
        return data;
    }

    double[][] ubahMatriks(double[] x) {
        double[][] bil = new double[hiddenLayer][dataLatih[0].length];
        int n = 0;
        for (int i = 0; i < bil.length; i++) {
            for (int j = 0; j < bil[0].length; j++) {
                bil[i][j]
                        = x[n];
                n++;
            }
        }
        return bil;
    }

    double[][] transposeMatrix(double[][] x) {
        double[][] matriks = new double[x[0].length][x.length];
        for (int i = 0; i < x[0].length; i++) {
            for (int j = 0; j < x.length; j++) {
                matriks[i][j] = x[j][i];
            }
        }
        return matriks;
    }

    double[][] outputHiddenLayer(double[][] data, double[][] bobot) {
        double[][] bobotTranspose = transposeMatrix(bobot);
        double[][] hInit = new double[data.length][bobotTranspose[0].length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < bobotTranspose[0].length; j++) {
                for (int k = 0; k < data[0].length; k++) {
                    hInit[i][j] += data[i][k] * bobotTranspose[k][j];
                }
            }
        }
        return hInit;
    }

    double[][] aktivasiSigmoidBiner(double[][] hinit) {
        double[][] h = new double[hinit.length][hinit[0].length];
        for (int i = 0; i < hinit.length; i++) {
            for (int j = 0; j < hinit[0].length; j++) {
                //h[i][j] = hinit[i][j];
                h[i][j] = (1 / (1 + Math.exp(-hinit[i][j])));
//                h[i][j] = (1 -Math.exp(-hinit[i][j]) / (1 + Math.exp(-hinit[i][j])));
            }
        }
        return h;
    }

    double[][] moorePenrose(double[][] h) {
        double[][] hT = transposeMatrix(h);
        double[][] hTH = new double[hT.length][h[0].length];
        for (int i = 0; i < hT.length; i++) {
            for (int j = 0; j < h[0].length; j++) {
                for (int k = 0; k < hT[0].length; k++) {
                    hTH[i][j] += hT[i][k] * h[k][j];
                }
            }
        }

        //invers
        if (hiddenLayer > 1) {
            double det = (hTH[0][0] * hTH[1][1]) - (hTH[0][1] * hTH[1][0]);
            double temp = hTH[0][0];
            hTH[0][0] = hTH[1][1];
            hTH[1][1] = temp;

            hTH[0][1] = -hTH[0][1];
            hTH[1][0] = -hTH[1][0];

            for (int i = 0; i < hTH.length; ++i) {
                for (int j = 0; j < hTH[0].length; ++j) {
                    hTH[i][j] = hTH[i][j] / det;
                }
            }
        }

        //hDagger
        double[][] hDagger = new double[hTH.length][hT[0].length];
        for (int i = 0; i < hTH.length; i++) {
            for (int j = 0; j < hT[0].length; j++) {
                for (int k = 0; k < hTH[0].length; k++) {
                    hDagger[i][j] += hTH[i][k] * hT[k][j];
                }
            }
        }
        return hDagger;
    }

    double[][] beta(double[][] hD, int[] t) {
        double[][] targetTraining = konversiKelas(t);
        double[][] b = new double[hD.length][targetTraining[0].length];
        for (int i = 0; i < hD.length; i++) {
            for (int j = 0; j < targetTraining[0].length; j++) {
                for (int k = 0; k < hD[0].length; k++) {
                    b[i][j] += hD[i][k] * targetTraining[k][j];
                    //System.out.println(b[i][j]);
                }
            }
        }
        return b;
    }

    double[][] outputLayer(double[][] h, double[][] b) {
        double[][] y = new double[h.length][b[0].length];
        for (int i = 0; i < h.length; i++) {
            for (int j = 0; j < b[0].length; j++) {
                for (int k = 0; k < h[0].length; k++) {
                    y[i][j] += h[i][k] * b[k][j];
                }
            }
        }
        return y;
    }

    int[] prediksi(double[] temp) {
        bobot = ubahMatriks(temp);
        hInit = outputHiddenLayer(dataNormalisasi, bobot);
        h = aktivasiSigmoidBiner(hInit);
        moorePenrose = moorePenrose(h);
        beta = beta(moorePenrose, kelasDataLatih);
        hInitTest = outputHiddenLayer(dataUjiNorm, bobot);
        hTest = aktivasiSigmoidBiner(hInitTest);
        double[][] outputLayer = outputLayer(hTest, beta);
        return this.hasilPrediksi(outputLayer);
    }

    int[] hasilPrediksi(double[][] outputLayer) {
        double max;
        double maxArray[] = new double[outputLayer.length];
        double[][] hasilPrediksi = new double[outputLayer.length][outputLayer[0].length];
        for (int i = 0; i < outputLayer.length; i++) {
            max = -99999;
            for (int j = 0; j < outputLayer[0].length; j++) {
                if (outputLayer[i][j] > max) {
                    max = outputLayer[i][j];
                }
            }
            maxArray[i] = max;
        }
        int[] kelas = new int[outputLayer.length];
        for (int i = 0; i < outputLayer.length; i++) {
            for (int j = 0; j < outputLayer[0].length; j++) {
                if (outputLayer[i][j] == maxArray[i]) {
                    kelas[i] = j + 1;
                }
            }
        }
        return kelas;
    }

    //1 -> 1 -1 -1
    double[][] konversiKelas(int[] k) {
        double[][] kelas = new double[k.length][jumlahKelas];
        for (int i = 0; i < kelas.length; i++) {
            for (int j = 0; j < kelas[0].length; j++) {
                if (k[i] == j + 1) {
                    kelas[i][j] = 1;
                } else {
                    kelas[i][j] = -1;
                }
            }
        }
        return kelas;
    }

    double akurasi(int[] hasil) {
        int prediksiBenar = 0;
        for (int i = 0; i < hasil.length; i++) {
            if (kelasDataUji[i] == hasil[i]) {
                prediksiBenar++;
            }
        }
        return (((double) prediksiBenar / kelasDataUji.length) * 100);
    }

    public void show(double[][] data, String nama) {
        System.out.println(nama);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                System.out.printf("%.3f%s", data[i][j], "\t");
            }
            System.out.println("");
        }
    }

    public void showHasil(double[] bobot) {
        int[] kelasPrediksi = this.prediksi(bobot);
        System.out.println("Hasil Prediksi Kelas");
        System.out.println("-----------------------------------------------");
        System.out.printf("%4s%9s%7s%8s%10s%9s\n", "JK", "Umur", "BB", "TB", "Aktual", "Prediksi");
        System.out.println("-----------------------------------------------");
        for (int i = 0; i < dataUji.length; i++) {
            for (int j = 0; j < dataUji[0].length; j++) {
                System.out.printf("%.3f%s", dataUji[i][j], "\t");
            }
            System.out.printf("%4s%7s%s", kelasDataUji[i], kelasPrediksi[i], "\n");
        }
        System.out.println("-----------------------------------------------");
        System.out.println("Akurasi = " + this.akurasi(kelasPrediksi) + "%");
    }
}
