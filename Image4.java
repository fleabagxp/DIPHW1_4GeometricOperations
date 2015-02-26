/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package image4;

/**
 *
 * @author fleabag
 */
import java.awt.Color;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.util.Scanner;
import javax.swing.JFrame;
import java.awt.*;

/**
 *
 * @author fleabag
 */
public class Image4 extends JFrame {

    /**
     * @param args the command line arguments
     */
    static int[] histogram = new int[256];
    static int picHeight, picWidth, maxvalue;
    static int x1, x2, x3, x4;
    static int y1, y2, y3, y4;

    public static void main(String[] args) throws FileNotFoundException, IOException {
        // TODO code application logic here
        String gridPGM = "D:\\Users\\fleabag\\Documents\\NetBeansProjects\\Image1\\src\\image1\\Image\\4\\grid.pgm";
        String disgridPGM = "D:\\Users\\fleabag\\Documents\\NetBeansProjects\\Image1\\src\\image1\\Image\\4\\distgrid.pgm";
        String distlennaPGM = "D:\\Users\\fleabag\\Documents\\NetBeansProjects\\Image1\\src\\image1\\Image\\4\\distlenna.pgm";
        FileInputStream file_grid = new FileInputStream(gridPGM);
        FileInputStream file_disgrid = new FileInputStream(disgridPGM);
        FileInputStream file_distlenna = new FileInputStream(distlennaPGM);
        int[][] data_grid = new int[256][256];
        int[][] data_disgrid = new int[256][256];
        int[][] data_distlenna = new int[256][256];

        Scanner scan_grid = new Scanner(file_grid);
        Scanner scan_disgrid = new Scanner(file_disgrid);
        Scanner scan_distlenna = new Scanner(file_distlenna);

        //Header Line 1
        scan_grid.nextLine();
        scan_disgrid.nextLine();
        scan_distlenna.nextLine();

        //Header Line 2
        scan_grid.nextLine();
        scan_disgrid.nextLine();
        scan_distlenna.nextLine();

        //Header Line 3
        picHeight = scan_grid.nextInt();
        picWidth = scan_grid.nextInt();
        maxvalue = scan_grid.nextInt();

        //Header Line 4
        scan_disgrid.nextLine();
        scan_disgrid.nextLine();
        scan_distlenna.nextLine();
        scan_distlenna.nextLine();

        file_grid.close();
        file_disgrid.close();
        file_distlenna.close();

        file_grid = new FileInputStream(gridPGM);
        file_disgrid = new FileInputStream(disgridPGM);
        file_distlenna = new FileInputStream(distlennaPGM);
        DataInputStream dis_grid = new DataInputStream(file_grid);
        DataInputStream dis_disgrid = new DataInputStream(file_disgrid);
        DataInputStream dis_distlenna = new DataInputStream(file_distlenna);

        int numnewlines = 4;
        while (numnewlines > 0) {
            char c, d, e;
            do {
                c = (char) (dis_grid.readUnsignedByte());
                d = (char) (dis_disgrid.readUnsignedByte());
                e = (char) (dis_distlenna.readUnsignedByte());
                System.out.print(c);
            } while (c != '\n');
            numnewlines--;
        }

        int a;
        int[][] data2D = new int[picWidth][picHeight];
        for (int row = 0; row < picWidth; row++) {
            for (int col = 0; col < picHeight; col++) {
                data_grid[row][col] = dis_grid.readUnsignedByte();
                data_disgrid[row][col] = dis_disgrid.readUnsignedByte();
                data_distlenna[row][col] = dis_distlenna.readUnsignedByte();
            }
        }

        int[][] convert = new int[data_grid.length][data_grid[0].length];
        for (int row = 0; row < data_grid.length; row++) {
            for (int col = 0; col < data_grid[0].length; col++) {
                convert[row][col] = 255 - data_grid[row][col];
            }
        }

        int[][] kernel = new int[][]{
            {0, 1, 0},
            {1, 1, 1},
            {0, 1, 0}};

        int[][] con = new int[data_grid.length][data_grid[0].length];
        con = Convolute(convert, kernel, new int[]{2, 2});

        int[][][] grid_nor = new int[17][17][2];
        grid_nor = findXY(con);

        double[][] xy = new double[4][4];
        double[] xb = new double[4];
        double[] yb = new double[4];
        double[] wx = new double[4];
        double[] wy = new double[4];
        int xp, yp;

        for (int row = 0; row < grid_nor.length - 1; row++) {
            for (int col = 0; col < grid_nor[0].length - 1; col++) {
                x1 = grid_nor[row][col][0];
                y1 = grid_nor[row][col][1];
                x2 = grid_nor[row][col + 1][0];
                y2 = grid_nor[row][col + 1][1];
                x3 = grid_nor[row + 1][col][0];
                y3 = grid_nor[row + 1][col][1];
                x4 = grid_nor[row + 1][col + 1][0];
                y4 = grid_nor[row + 1][col + 1][1];

                //xy'1
                xy[0][0] = x1;
                xy[0][1] = y1;
                xy[0][2] = x1 * y1;
                xy[0][3] = 1;

                //xy'2
                xy[1][0] = x2;
                xy[1][1] = y2;
                xy[1][2] = x2 * y2;
                xy[1][3] = 1;

                //xy'3
                xy[2][0] = x3;
                xy[2][1] = y3;
                xy[2][2] = x3 * y3;
                xy[2][3] = 1;

                //xy'4
                xy[3][0] = x4;
                xy[3][1] = y4;
                xy[3][2] = x4 * y4;
                xy[3][3] = 1;

                //x'
                xb[0] = gridcon[row][col][1];
                xb[1] = gridcon[row][col][3];
                xb[2] = gridcon[row][col][5];
                xb[3] = gridcon[row][col][7];

                //y'
                yb[0] = gridcon[row][col][2];
                yb[1] = gridcon[row][col][4];
                yb[2] = gridcon[row][col][6];
                yb[3] = gridcon[row][col][8];

                double[][] v = new double[xy.length][];
                for (int i = 0; i < xy.length; i++) {
                    v[i] = xy[i].clone();
                }
                wx = gaussianElimination(v, xb);

                for (int i = 0; i < xy.length; i++) {
                    v[i] = xy[i].clone();
                }
                wy = gaussianElimination(v, yb);

                for (int y = y1; y < y3; y++) {
                    for (int x = x1; x < x2; x++) {
                        xp = (int) Math.round(wx[0] * x + wx[1] * y + wx[2] * x * y + wx[3]);
                        yp = (int) Math.round(wy[0] * x + wy[1] * y + wy[2] * x * y + wy[3]);
                        if (xp > 255) {
                            xp = 255;
                        }
                        if (yp > 255) {
                            yp = 255;
                        }
                        //distlenna
                        //data2D[y][x] = data_distlenna[yp][xp];
                        //disgrid
                        data2D[y][x] = data_disgrid[yp][xp];
                        a = data2D[y][x];
                        histogram[a]++;
                        System.out.print(data2D[y][x] + " ");
                    }
                }
                System.out.println();
            }
        }
        Image4 image = new Image4();
        showPGM pgm = new showPGM(data2D);
    }

    public Image4() {
        super("Histogram");
        Dimension size = new Dimension(300, 350);
        Toolkit tk = getToolkit();
        Dimension screen = tk.getScreenSize();
        setBounds((screen.width - size.width) / 2, (screen.height - size.height) / 2, size.width, size.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Color.GRAY);
        setVisible(true);
    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        g.setColor(Color.WHITE);

        if (histogram != null) {
            int widths = 45;
            int height = 350;
            int HhPos = (widths - (widths / 2));
            int HvPos = 342;
            for (int i = 0; i <= 255; i++) {
                int[] r = histogram;

                r[i] = (r[i] * 100) / 300;
                g.fillRect(i + HhPos, HvPos, 1, -r[i]);
            }
        }
    }

    public static int[][] Convolute(int[][] con, int[][] kernel, int[] origin) {
        int top = (int) Math.floor(kernel.length / 2);
        int left = (int) Math.floor(kernel[0].length / 2);
        int[][] c = new int[(int) (con.length + top * 2)][(int) (con[0].length + left * 2)];
        for (int row = top; row < c.length - top; row++) {
            for (int col = left; col < c[0].length - left; col++) {
                c[row][col] = con[row - top][col - left];
            }
        }

        int max = 0;
        for (int cy = top; cy < c.length - top; cy++) {
            for (int cx = left; cx < c[0].length - left; cx++) {
                int val = 0;
                for (int row = 0; row < kernel.length; row++) {
                    for (int col = 0; col < kernel[0].length; col++) {
                        val += kernel[row][col] * c[(cy - top) + row][(cx - left) + col];
                    }
                }
                if (max < val) {
                    max = val;
                }
                if (val > 1000) {
                    val = 255;
                } else {
                    val = 0;
                }
                con[cy - top][cx - left] = val;
            }
        }
        return con;
    }

    public static int[][][] findXY(int[][] findXY) {
        int[][][] grid_nor = new int[17][17][2];
        int x;
        int y = 1;
        for (int i = 0; i < 17; i++) {
            grid_nor[0][i][0] = i * 16;
            grid_nor[0][i][1] = 0;
            if (i != 0) {
                grid_nor[0][i][0]--;
            }
        }
        for (int row = 0; row < findXY.length; row++) {
            grid_nor[y][0][0] = 0;
            grid_nor[y][0][1] = y * 16;
            x = 1;
            for (int col = 0; col < findXY[0].length; col++) {
                if (findXY[row][col] == 255) {
                    grid_nor[y][x][0] = col;
                    grid_nor[y][x++][1] = row;
                    if (x == 16) {
                        x = 1;
                        grid_nor[y][16][0] = 255;
                        grid_nor[y][16][1] = y * 16;
                        y++;
                    }
                }
            }
        }
        for (int i = 0; i < 17; i++) {
            grid_nor[16][i][0] = i * 16;
            grid_nor[16][i][1] = 255;
            if (i != 0) {
                grid_nor[16][i][0]--;
            }
        }
        return grid_nor;
    }

    public static double[] gaussianElimination(double[][] A, double[] b) {
        double EPSILON = 1e-10;
        int N = b.length;

        for (int p = 0; p < N; p++) {
            int max = p;
            for (int i = p + 1; i < N; i++) {
                if (Math.abs(A[i][p]) > Math.abs(A[max][p])) {
                    max = i;
                }
            }
            double[] temp = A[p];
            A[p] = A[max];
            A[max] = temp;
            double t = b[p];
            b[p] = b[max];
            b[max] = t;

            if (Math.abs(A[p][p]) <= EPSILON) {
                throw new RuntimeException("Matrix is singular or nearly singular");
            }

            for (int i = p + 1; i < N; i++) {
                double alpha = A[i][p] / A[p][p];
                b[i] -= alpha * b[p];
                for (int j = p; j < N; j++) {
                    A[i][j] -= alpha * A[p][j];
                }
            }
        }

        double[] x = new double[N];
        for (int i = N - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < N; j++) {
                sum += A[i][j] * x[j];
            }
            x[i] = (b[i] - sum) / A[i][i];
        }
        return x;
    }

    public static int[][][] gridcon = new int[][][]{
        //{x1,y1,x2,y2,x3,y3,x4,y4}
        {{0, 0, 0, 16, 0, 0, 16, 16, 16},
        {1, 16, 0, 32, 0, 16, 16, 32, 16},
        {2, 32, 0, 48, 0, 32, 16, 48, 16},
        {3, 48, 0, 64, 0, 48, 16, 64, 16},
        {4, 64, 0, 80, 0, 64, 16, 79, 16},
        {5, 80, 0, 96, 0, 79, 16, 97, 17},
        {6, 96, 0, 112, 0, 97, 17, 114, 19},
        {7, 112, 0, 128, 0, 114, 19, 130, 18},
        {8, 128, 0, 144, 0, 130, 18, 146, 19},
        {9, 144, 0, 160, 0, 146, 19, 160, 18},
        {10, 160, 0, 176, 0, 160, 18, 176, 17},
        {11, 176, 0, 192, 0, 176, 17, 192, 16},
        {12, 192, 0, 208, 0, 192, 16, 208, 16},
        {13, 208, 0, 224, 0, 208, 16, 224, 16},
        {14, 224, 0, 240, 0, 224, 16, 240, 16},
        {15, 240, 0, 256, 0, 240, 16, 256, 16}},
        {{16, 0, 16, 16, 16, 0, 32, 16, 32},
        {17, 16, 16, 32, 16, 16, 32, 33, 32},
        {18, 32, 16, 48, 16, 33, 32, 48, 32},
        {19, 48, 16, 64, 16, 48, 32, 67, 31},
        {20, 64, 16, 79, 16, 67, 31, 85, 35},
        {21, 79, 16, 97, 17, 85, 35, 103, 37},
        {22, 97, 17, 114, 19, 103, 37, 121, 40},
        {23, 114, 19, 130, 18, 121, 40, 136, 42},
        {24, 130, 18, 146, 19, 136, 42, 150, 43},
        {25, 146, 19, 160, 18, 150, 43, 162, 41},
        {26, 160, 18, 176, 17, 162, 41, 177, 37},
        {27, 176, 17, 192, 16, 177, 37, 192, 35},
        {28, 192, 16, 208, 16, 192, 35, 208, 32},
        {29, 208, 16, 224, 16, 208, 32, 224, 32},
        {30, 224, 16, 240, 16, 224, 32, 240, 31},
        {31, 240, 16, 256, 16, 240, 31, 256, 32}},
        {{32, 0, 32, 16, 32, 0, 48, 16, 48},
        {33, 16, 32, 33, 32, 16, 48, 32, 48},
        {34, 33, 32, 48, 32, 32, 48, 51, 49},
        {35, 48, 32, 67, 31, 51, 49, 72, 49},
        {36, 67, 31, 85, 35, 72, 49, 94, 53},
        {37, 85, 35, 103, 37, 94, 53, 112, 56},
        {38, 103, 37, 121, 40, 112, 56, 128, 60},
        {39, 121, 40, 136, 42, 128, 60, 141, 63},
        {40, 136, 42, 150, 43, 141, 63, 154, 65},
        {41, 150, 43, 162, 41, 154, 65, 166, 65},
        {42, 162, 41, 177, 37, 166, 65, 178, 62},
        {43, 177, 37, 192, 35, 178, 62, 192, 57},
        {44, 192, 35, 208, 32, 192, 57, 206, 52},
        {45, 208, 32, 224, 32, 206, 52, 224, 48},
        {46, 224, 32, 240, 31, 224, 48, 240, 48},
        {47, 240, 31, 256, 32, 240, 48, 256, 48}},
        {{48, 0, 48, 16, 48, 0, 64, 16, 64},
        {49, 16, 48, 32, 48, 16, 64, 34, 64},
        {50, 32, 48, 51, 49, 34, 64, 56, 63},
        {51, 51, 49, 72, 49, 56, 63, 80, 66},
        {52, 72, 49, 94, 53, 80, 66, 99, 68},
        {53, 94, 53, 112, 56, 99, 68, 116, 72},
        {54, 112, 56, 128, 60, 116, 72, 132, 76},
        {55, 128, 60, 141, 63, 132, 76, 144, 80},
        {56, 141, 63, 154, 65, 144, 80, 156, 84},
        {57, 154, 65, 166, 65, 156, 84, 167, 85},
        {58, 166, 65, 178, 62, 167, 85, 177, 83},
        {59, 178, 62, 192, 57, 177, 83, 190, 80},
        {60, 192, 57, 206, 52, 190, 80, 204, 74},
        {61, 206, 52, 224, 48, 204, 74, 222, 66},
        {62, 224, 48, 240, 48, 222, 66, 240, 64},
        {63, 240, 48, 256, 48, 240, 64, 256, 64}},
        {{64, 0, 64, 16, 64, 0, 80, 16, 80},
        {65, 16, 64, 34, 64, 16, 80, 37, 78},
        {66, 34, 64, 56, 63, 37, 78, 63, 78},
        {67, 56, 63, 80, 66, 63, 78, 84, 78},
        {68, 80, 66, 99, 68, 84, 78, 103, 81},
        {69, 99, 68, 116, 72, 103, 81, 119, 85},
        {70, 116, 72, 132, 76, 119, 85, 132, 89},
        {71, 132, 76, 144, 80, 132, 89, 144, 94},
        {72, 144, 80, 156, 84, 144, 94, 154, 100},
        {73, 156, 84, 167, 85, 154, 100, 165, 103},
        {74, 167, 85, 177, 83, 165, 103, 176, 102},
        {75, 177, 83, 190, 80, 176, 102, 188, 100},
        {76, 190, 80, 204, 74, 188, 100, 203, 94},
        {77, 204, 74, 222, 66, 203, 94, 221, 85},
        {78, 222, 66, 240, 64, 221, 85, 240, 80},
        {79, 240, 64, 256, 64, 240, 80, 256, 80}},
        {{80, 0, 80, 16, 80, 0, 96, 16, 96},
        {81, 16, 80, 37, 78, 16, 96, 41, 93},
        {82, 37, 78, 63, 78, 41, 93, 65, 91},
        {83, 63, 78, 84, 78, 65, 91, 86, 90},
        {84, 84, 78, 103, 81, 86, 90, 102, 90},
        {85, 103, 81, 119, 85, 102, 90, 118, 96},
        {86, 119, 85, 132, 89, 118, 96, 130, 102},
        {87, 132, 89, 144, 94, 130, 102, 141, 108},
        {88, 144, 94, 154, 100, 141, 108, 152, 116},
        {89, 154, 100, 165, 103, 152, 116, 161, 117},
        {90, 165, 103, 176, 102, 161, 117, 172, 119},
        {91, 176, 102, 188, 100, 172, 119, 184, 116},
        {92, 188, 100, 203, 94, 184, 116, 200, 112},
        {93, 203, 94, 221, 85, 200, 112, 217, 105},
        {94, 221, 85, 240, 80, 217, 105, 237, 97},
        {95, 240, 80, 256, 80, 237, 97, 256, 96}},
        {{96, 0, 96, 16, 96, 0, 112, 18, 110},
        {97, 16, 96, 41, 93, 18, 110, 42, 106},
        {98, 41, 93, 65, 91, 42, 106, 65, 103},
        {99, 65, 91, 86, 90, 65, 103, 84, 101},
        {100, 86, 90, 102, 90, 84, 101, 100, 102},
        {101, 102, 90, 118, 96, 100, 102, 114, 105},
        {102, 118, 96, 130, 102, 114, 105, 127, 112},
        {103, 130, 102, 141, 108, 127, 112, 136, 119},
        {104, 141, 108, 152, 116, 136, 119, 145, 126},
        {105, 152, 116, 161, 117, 145, 126, 154, 130},
        {106, 161, 117, 172, 119, 154, 130, 167, 132},
        {107, 172, 119, 184, 116, 167, 132, 180, 132},
        {108, 184, 116, 200, 112, 180, 132, 196, 128},
        {109, 200, 112, 217, 105, 196, 128, 215, 122},
        {110, 217, 105, 237, 97, 215, 122, 237, 115},
        {111, 237, 97, 256, 96, 237, 115, 256, 112}},
        {{112, 0, 112, 18, 110, 0, 128, 19, 126},
        {113, 18, 110, 42, 106, 19, 126, 41, 120},
        {114, 42, 106, 65, 103, 41, 120, 64, 113},
        {115, 65, 103, 84, 101, 64, 113, 81, 112},
        {116, 84, 101, 100, 102, 81, 112, 96, 112},
        {117, 100, 102, 114, 105, 96, 112, 109, 115},
        {118, 114, 105, 127, 112, 109, 115, 121, 120},
        {119, 127, 112, 136, 119, 121, 120, 129, 128},
        {120, 136, 119, 145, 126, 129, 128, 137, 135},
        {121, 145, 126, 154, 130, 137, 135, 148, 141},
        {122, 154, 130, 167, 132, 148, 141, 161, 143},
        {123, 167, 132, 180, 132, 161, 143, 174, 144},
        {124, 180, 132, 196, 128, 174, 144, 193, 142},
        {125, 196, 128, 215, 122, 193, 142, 213, 137},
        {126, 215, 122, 237, 115, 213, 137, 236, 131},
        {127, 237, 115, 256, 112, 236, 131, 256, 128}},
        {{128, 0, 128, 19, 126, 0, 144, 18, 141},
        {129, 19, 126, 41, 120, 18, 141, 40, 135},
        {130, 41, 120, 64, 113, 40, 135, 60, 129},
        {131, 64, 113, 81, 112, 60, 129, 76, 125},
        {132, 81, 112, 96, 112, 76, 125, 90, 124},
        {133, 96, 112, 109, 115, 90, 124, 101, 125},
        {134, 109, 115, 121, 120, 101, 125, 113, 129},
        {135, 121, 120, 129, 128, 113, 129, 121, 136},
        {136, 129, 128, 137, 135, 121, 136, 131, 144},
        {137, 137, 135, 148, 141, 131, 144, 142, 150},
        {138, 148, 141, 161, 143, 142, 150, 156, 154},
        {139, 161, 143, 174, 144, 156, 154, 172, 154},
        {140, 174, 144, 193, 142, 172, 154, 190, 154},
        {141, 193, 142, 213, 137, 190, 154, 212, 149},
        {142, 213, 137, 236, 131, 212, 149, 236, 145},
        {143, 236, 131, 256, 128, 236, 145, 256, 144}},
        {{144, 0, 144, 18, 141, 0, 160, 17, 160},
        {145, 18, 141, 40, 135, 17, 160, 38, 151},
        {146, 40, 135, 60, 129, 38, 151, 57, 144},
        {147, 60, 129, 76, 125, 57, 144, 72, 140},
        {148, 76, 125, 90, 124, 72, 140, 85, 138},
        {149, 90, 124, 101, 125, 85, 138, 96, 138},
        {150, 101, 125, 113, 129, 96, 138, 106, 141},
        {151, 113, 129, 121, 136, 106, 141, 115, 148},
        {152, 121, 136, 131, 144, 115, 148, 126, 153},
        {153, 131, 144, 142, 150, 126, 153, 138, 161},
        {154, 142, 150, 156, 154, 138, 161, 153, 165},
        {155, 156, 154, 172, 154, 153, 165, 169, 167},
        {156, 172, 154, 190, 154, 169, 167, 190, 167},
        {157, 190, 154, 212, 149, 190, 167, 214, 163},
        {158, 212, 149, 236, 145, 214, 163, 238, 161},
        {159, 236, 145, 256, 144, 238, 161, 256, 160}},
        {{160, 0, 160, 17, 160, 0, 176, 16, 177},
        {161, 17, 160, 38, 151, 16, 177, 34, 170},
        {162, 38, 151, 57, 144, 34, 170, 53, 162},
        {163, 57, 144, 72, 140, 53, 162, 66, 156},
        {164, 72, 140, 85, 138, 66, 156, 81, 153},
        {165, 85, 138, 96, 138, 81, 153, 92, 153},
        {166, 96, 138, 106, 141, 92, 153, 102, 156},
        {167, 106, 141, 115, 148, 102, 156, 112, 158},
        {168, 115, 148, 126, 153, 112, 158, 124, 165},
        {169, 126, 153, 138, 161, 124, 165, 137, 171},
        {170, 138, 161, 153, 165, 137, 171, 153, 174},
        {171, 153, 165, 169, 167, 153, 174, 171, 178},
        {172, 169, 167, 190, 167, 171, 178, 192, 178},
        {173, 190, 167, 214, 163, 192, 178, 217, 177},
        {174, 214, 163, 238, 161, 217, 177, 240, 176},
        {175, 238, 161, 256, 160, 240, 176, 256, 176}},
        {{176, 0, 176, 16, 177, 0, 192, 17, 192},
        {177, 16, 177, 34, 170, 17, 192, 33, 191},
        {178, 34, 170, 53, 162, 33, 191, 51, 182},
        {179, 53, 162, 66, 156, 51, 182, 66, 175},
        {180, 66, 156, 81, 153, 66, 175, 78, 170},
        {181, 81, 153, 92, 153, 78, 170, 90, 169},
        {182, 92, 153, 102, 156, 90, 169, 101, 172},
        {183, 102, 156, 112, 158, 101, 172, 113, 176},
        {184, 112, 158, 124, 165, 113, 176, 124, 181},
        {185, 124, 165, 137, 171, 124, 181, 139, 184},
        {186, 137, 171, 153, 174, 139, 184, 155, 188},
        {187, 153, 174, 171, 178, 155, 188, 174, 189},
        {188, 171, 178, 192, 178, 174, 189, 198, 193},
        {189, 192, 178, 217, 177, 198, 193, 221, 192},
        {190, 217, 177, 240, 176, 221, 192, 240, 192},
        {191, 240, 176, 256, 176, 240, 192, 256, 192}},
        {{192, 0, 192, 17, 192, 0, 208, 16, 208},
        {193, 17, 192, 33, 191, 16, 208, 31, 208},
        {194, 33, 191, 51, 182, 31, 208, 49, 204},
        {195, 51, 182, 66, 175, 49, 204, 64, 197},
        {196, 66, 175, 78, 170, 64, 197, 80, 193},
        {197, 78, 170, 90, 169, 80, 193, 89, 190},
        {198, 90, 169, 101, 172, 89, 190, 101, 190},
        {199, 101, 172, 113, 176, 101, 190, 113, 191},
        {200, 113, 176, 124, 181, 113, 191, 128, 195},
        {201, 124, 181, 139, 184, 128, 195, 144, 198},
        {202, 139, 184, 155, 188, 144, 198, 161, 203},
        {203, 155, 188, 174, 189, 161, 203, 182, 205},
        {204, 174, 189, 198, 193, 182, 205, 204, 206},
        {205, 198, 193, 221, 192, 204, 206, 224, 208},
        {206, 221, 192, 240, 192, 224, 208, 240, 208},
        {207, 240, 192, 256, 192, 240, 208, 256, 208}},
        {{208, 0, 208, 16, 208, 0, 224, 16, 224},
        {209, 16, 208, 31, 208, 16, 224, 32, 224},
        {210, 31, 208, 49, 204, 32, 224, 48, 223},
        {211, 49, 204, 64, 197, 48, 223, 63, 221},
        {212, 64, 197, 80, 193, 63, 221, 80, 217},
        {213, 80, 193, 89, 190, 80, 217, 92, 213},
        {214, 89, 190, 101, 190, 92, 213, 106, 212},
        {215, 101, 190, 113, 191, 106, 212, 119, 212},
        {216, 113, 191, 128, 195, 119, 212, 133, 215},
        {217, 128, 195, 144, 198, 133, 215, 150, 217},
        {218, 144, 198, 161, 203, 150, 217, 168, 220},
        {219, 161, 203, 182, 205, 168, 220, 189, 222},
        {220, 182, 205, 204, 206, 189, 222, 208, 224},
        {221, 204, 206, 224, 208, 208, 224, 223, 224},
        {222, 224, 208, 240, 208, 223, 224, 241, 224},
        {223, 240, 208, 256, 208, 241, 224, 256, 224}},
        {{224, 0, 224, 16, 224, 0, 240, 16, 240},
        {225, 16, 224, 32, 224, 16, 240, 32, 240},
        {226, 32, 224, 48, 223, 32, 240, 48, 240},
        {227, 48, 223, 63, 221, 48, 240, 64, 240},
        {228, 63, 221, 80, 217, 64, 240, 80, 239},
        {229, 80, 217, 92, 213, 80, 239, 95, 238},
        {230, 92, 213, 106, 212, 95, 238, 110, 237},
        {231, 106, 212, 119, 212, 110, 237, 125, 236},
        {232, 119, 212, 133, 215, 125, 236, 142, 237},
        {233, 133, 215, 150, 217, 142, 237, 158, 238},
        {234, 150, 217, 168, 220, 158, 238, 175, 239},
        {235, 168, 220, 189, 222, 175, 239, 192, 240},
        {236, 189, 222, 208, 224, 192, 240, 208, 240},
        {237, 208, 224, 223, 224, 208, 240, 224, 240},
        {238, 223, 224, 241, 224, 224, 240, 240, 240},
        {239, 241, 224, 256, 224, 240, 240, 256, 240}},
        {{240, 0, 240, 16, 240, 0, 256, 16, 256},
        {241, 16, 240, 32, 240, 16, 256, 32, 256},
        {242, 32, 240, 48, 240, 32, 256, 48, 256},
        {243, 48, 240, 64, 240, 48, 256, 64, 256},
        {244, 64, 240, 80, 239, 64, 256, 80, 256},
        {245, 80, 239, 95, 238, 80, 256, 96, 256},
        {246, 95, 238, 110, 237, 96, 256, 112, 256},
        {247, 110, 237, 125, 236, 112, 256, 128, 256},
        {248, 125, 236, 142, 237, 128, 256, 144, 256},
        {249, 142, 237, 158, 238, 144, 256, 160, 256},
        {250, 158, 238, 175, 239, 160, 256, 176, 256},
        {251, 175, 239, 192, 240, 176, 256, 192, 256},
        {252, 192, 240, 208, 240, 192, 256, 208, 256},
        {253, 208, 240, 224, 240, 208, 256, 224, 256},
        {254, 224, 240, 240, 240, 224, 256, 240, 256},
        {255, 240, 240, 256, 240, 240, 256, 256, 256}}};
}
