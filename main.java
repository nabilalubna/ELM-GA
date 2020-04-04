/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GiziRapih;

import java.io.File;

/**
 *
 * @author nabilalubnairbakanisa
 */
public class main {

    public static void main(String[] args) throws Exception {
        int popsize = 100;
        int iterasi = 34;
        double cr = 0.6;
        double mr = 0.4;
        int hiddenLayer = 2;
        File file = new File("/Users/nabilalubnairbakanisa/NetBeansProjects/StatusGiziAnak/src/DATAFIXPAKE.xls");
        int jumlahKelas = 8;
        AlgoritmeGenetika test = new AlgoritmeGenetika(popsize, iterasi, cr, mr,
                hiddenLayer, file, jumlahKelas);
    }
}
