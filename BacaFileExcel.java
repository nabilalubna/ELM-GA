import java.io.File;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;

public class BacaFileExcel {

    File file;

    public BacaFileExcel(File file) throws Exception{
        this.file = file;
    }

    public double[][] getData(int x) throws Exception {
        Workbook wb = Workbook.getWorkbook(file);
        Sheet s = wb.getSheet(x);
        int row = s.getRows();
        int col = s.getColumns()-1;
        double[][] data = new double[row][col];
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < col; j++) {
                Cell c = s.getCell(j, i);
                data[i][j] = Double.parseDouble(c.getContents());
            }
        }
        return data;
    }
    
    public int[] getKelasData(int x) throws Exception {
        Workbook wb = Workbook.getWorkbook(file);
        Sheet s = wb.getSheet(x);
        int row = s.getRows();
        int col = s.getColumns();
        int[] kelas = new int[row];
        for (int i = 0; i < kelas.length; i++) {
            Cell c = s.getCell(col-1, i);
            kelas[i] = (int) Double.parseDouble(c.getContents());
        }
        return kelas; 
    }
}
