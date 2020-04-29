package com.jos3ocrt3s.appbluetoothme.filters;

import android.database.Cursor;
import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Filters {


    private final static String TAG = Filters.class.getSimpleName();
    //DataBase myDb;
    //private volatile double sum = 0;
    //private volatile int den = 0;
    //public double result = 0;
    List<Double> ListaData = new ArrayList<Double>();//creates a list to hold the data of the maf
    List<Double> ListaDataIOT = new ArrayList<Double>();//creates a list to hold the data of the IOT
    List<Double> MovAv = new ArrayList<Double>();
    List<Double> MovIOT = new ArrayList<Double>();
    List<Integer> MovBinned = new ArrayList<Integer>();//creates a list to hold the data after being binned
    List<Integer> MovHistogram = new ArrayList<Integer>();//creates a list to hold the data after passing trhough the histogram
    List<Integer> MovHistogramNegative = new ArrayList<Integer>();//creates a list to hold the data after passing trhough the histogram
    List<Double> ListMeanVar = new ArrayList<Double>();//creates a list to hold the data after passing trhough the Mean and variance
    List<Double> BlackMan1 = new ArrayList<Double>();//creates a list to hold the data got from the convolution
    List<List<Double>> MeanWindow = new ArrayList<List<Double>>();//creates a list to hold the data in 2 dimensions
    List<Double> MeanWindowDimension = new ArrayList<Double>();//creates a list to hold the data 2 dimensional for each dimension
    List<Double> MeanWindowValue = new ArrayList<Double>();//creates a list to hold the data value from the window mean
    List<Double> SDWindowValue = new ArrayList<Double>();//creates a list to hold the data value from the window SD
    List<Double> ListMeanWindow = new ArrayList<Double>();//creates a list to hold the data value from the window SD
    public Double[] H = new Double[101];
    public Double[] HP = new Double[101];//higher order filter
    public Double[] HF = new Double[101];//full order filter
    //GraphView graph;
    //LineGraphSeries<DataPoint> series;

    public List<Double> MAF1(Cursor Data) {//METHOD TO IMPLEMENT FILTER MOVING AVERAGE FILTER EQUIVALENT TO A LOW PASS FILTER
        Log.w(TAG, "MAF OBTENIDO");
        Double x, y;//VARIABLES WHICH WILL BE USED FOR MATH PURPOSES
        int z;
        z = 0;
        x = 0.0;
        y = 0.0;
        //ListaData.clear();
        if (Data.getCount() != 0) {
            Log.w(TAG, "MAF OBTENIDO");

            MovAv.clear();//clean the list
            for (Data.moveToFirst(); !Data.isAfterLast(); Data.moveToNext()) {//put the cursor into a list
                // The Cursor is now set to the right position
                MovAv.add(Double.parseDouble(Data.getString(1)));
            }
            for (int i = 0; i < MovAv.size() - 4; i++) {// for some reason the there is garbage data at the beggining,i is set to 10 to avoid that data

                y = (MovAv.get(z) + MovAv.get(z + 1) + MovAv.get(z + 2) + MovAv.get(z + 3)) / 4;//FUNCTION FOR MOVING AVERAGE FILTER
                z++;//
                Log.w(TAG, "MOVING AVERAGE FILTER: " + y + "primer numer: "+MovAv.get(z)+"ultimo numero: "+MovAv.get(z + 3));
                ListaData.add(y);
                Log.w(TAG, "congratulations");

            }
            return ListaData;

        }
        else
            ListaData.clear();
        return ListaData ;

    }



    public List<Double> IOT(Cursor Data ){

        Log.w(TAG, "IOT OBTENIDO");
        Double x,u, y;//VARIABLES WHICH WILL BE USED FOR MATH PURPOSES

        u= 0.3;// cut  off frequency of the sampling rate
        x = 0.0;
        y = 0.0;
        //ListaData.clear();
        if (Data.getCount() != 0) {
            Log.w(TAG, "IOT OBTENIDO");

            MovIOT.clear();//clean the list
            for (Data.moveToFirst(); !Data.isAfterLast(); Data.moveToNext()) {//put the cursor into a list
                // The Cursor is now set to the right position
                MovIOT.add(Double.parseDouble(Data.getString(1)));
            }
            ListaDataIOT.add(u*MovIOT.get(0));
            for (int i = 1; i < MovIOT.size(); i++) {
                y = (ListaDataIOT.get(i-1) + u*(MovIOT.get(i) - MovIOT.get(i - 1)));//FUNCTION FOR MOVING AVERAGE FILTER
                ListaDataIOT.add(y);
                Log.w(TAG, "congratulations");

            }
            return ListaDataIOT;

        }
        else

            return ListaDataIOT ;

    }

    public List<Double> BlackManFilter(Cursor Data ){

        Log.w(TAG, "BMF OBTENIDO");
        Double x,u, y, pi, sum;//VARIABLES WHICH WILL BE USED FOR MATH PURPOSES
        int M;
        pi = 3.14159265;
        u= 0.15;// cut  off frequency of the sampling rate between 0 and 0.5
        x = 0.0;
        y = 0.0;
        M = 100; // sets the length of the filter to 101 points
        sum = 0.0;
        //ListaData.clear();
        if (Data.getCount() != 0) {
            Log.w(TAG, "BLACKMAN  OBTENIDO");

            MovAv.clear();//clean the list
            for (Data.moveToFirst(); !Data.isAfterLast(); Data.moveToNext()) {//put the cursor into a list
                // The Cursor is now set to the right position
                MovAv.add(Double.parseDouble(Data.getString(1)));
            }
            Double[] BlackMan = new Double[MovAv.size()];
            Arrays.fill(BlackMan, 0.0);
            Arrays.fill(H, 0.0);//inicia el array en zero's
            Log.w(TAG, "size of the data is: "+ MovAv.size());
            Log.w(TAG, "size of the Double is: "+ BlackMan.length);
            for (int i = 0; i <= M; i++) {//calculates the filter kernel
                //res.moveToNext();
                //while (res.moveToNext()) {//put the list into the table of MAF
                //z++;
                //x = res.getInt(0);
                //
                if (i-M/2 == 0){
                    H[i] = 2*pi*u;
                    Log.w(TAG, "Media");
                }
                else
                    H[i] = Math.sin(2*pi*u*(i-M/2))/(i-M/2);
                //H[i] = H[i]*(0.54-0.46*Math.cos(2*pi*i/M));
                H[i] = H[i]*(0.42-0.5*Math.cos(2*pi*i/M)+0.08*Math.cos(4*pi*i/M));

            }

            Log.w(TAG, "BLACKMAN FILTER  OBTENIDO");

            for (int i1= 0; i1 <= M; i1++){//normalize the low pass filter kernel for unity dc gain
                sum = sum + H[i1];
            }
            for (int i2= 0; i2 <= M; i2++){
                H[i2] = H[i2]/sum;
            }

            //////////////////////High pass filter
            Arrays.fill(HP, 0.0);
            Double sum1, u1;
            sum1 = 0.0;
            u1 = 0.002;//cut-off frequency set at .2Hz
            for (int i11 = 0; i11 <= M; i11++) {//calculates the filter kernel
                if (i11-M/2 == 0){
                    HP[i11] = 2*pi*u1;
                    Log.w(TAG, "Media");
                }
                else{
                    HP[i11] = Math.sin(2*pi*u1*(i11-M/2))/(i11-M/2);}//window formula

                HP[i11] = HP[i11]*(0.42-0.5*Math.cos(2*pi*i11/M)+0.08*Math.cos(4*pi*i11/M));//Blackman filter formula

            }

            Log.w(TAG, "BLACKMAN HIGH FILTER  OBTENIDO");

            for (int i12= 0; i12 <= M; i12++){//normalize the low pass filter kernel for unity dc gain
                sum1 = sum1 + HP[i12];
            }
            for (int i13= 0; i13 <= M; i13++){
                HP[i13] = -HP[i13]/sum1;
            }
            HP[M/2] = HP[M/2]+1;//TO TURN A LOW PASS FILTER INTO A HIGH PASS FILTER I MUST TURN EACH SAMPLE TO ITS NEGATIVE FORM AND SUM 1 TO THE SAMPLE WHICH LAYS IN ITS SYMMETRY

            /////////////////////End high pass filter
            //BAND PASS FILTER
            Arrays.fill(HF, 0.0);
            for (int i21 = 0; i21 < M; i21++){
                HF[i21] = H[i21] +HP[i21];
            }
            /////////////////////////
            BlackMan1.clear();//makes suer there is nothing within the blackman1 list
            for (int i3= M; i3 < MovAv.size(); i3++){//convolve the input signal with the fiilter kernel
                BlackMan[i3]=  0.0;
                for (int i4= 0; i4 <= M; i4++){
                    BlackMan[i3]= BlackMan[i3]+ MovAv.get(i3-i4)*H[i4];//+ MovAv.get(i3-i4)*H[i4];

                }

                BlackMan1.add(BlackMan[i3]);
            }
            return BlackMan1;
        }
        else return BlackMan1 ;

    }

    public String hexaStringToInteger(String input){//turn hex values into integers
        double res = 0;
        String resConverted= "";//converts the value into a string
        int length = input.length()-1;
        for(int i=0;i<length+1;i++){
            char currNumber = input.charAt(i);
            switch (currNumber){

                case '0':
                    res += 0 * Math.pow(16,length-i);
                    break;
                case '1':
                    res += 1 * Math.pow(16,length-i);
                    break;
                case '2':
                    res += 2 * Math.pow(16,length-i);
                    break;
                case '3':
                    res += 3 * Math.pow(16,length-i);
                    break;
                case '4':
                    res += 4 * Math.pow(16,length-i);
                    break;
                case '5':
                    res += 5 * Math.pow(16,length-i);
                    break;
                case '6':
                    res += 6 * Math.pow(16,length-i);
                    break;
                case '7':
                    res += 7 * Math.pow(16,length-i);
                    break;
                case '8':
                    res += 8 * Math.pow(16,length-i);
                    break;
                case '9':
                    res += 9 * Math.pow(16,length-i);
                    break;
                case 'A':
                    res += 10 * Math.pow(16,length-i);
                    break;
                case 'B':
                    res += 11 * Math.pow(16,length-i);
                    break;
                case 'C':
                    res += 12 * Math.pow(16,length-i);
                    break;
                case 'D':
                    res += 13 * Math.pow(16,length-i);
                    break;
                case 'E':
                    res += 14 * Math.pow(16,length-i);
                    break;
                case 'F':
                    res += 15 * Math.pow(16,length-i);
                    break;
                case ' ':
                    break;
                default:
                    int number = Integer.parseInt(currNumber + "");
                    res += number * Math.pow(16,length-i);
                    break;
            }
        }

        DecimalFormat mFormat =  new DecimalFormat("#.####");
        if (res >4095){//complement 2 for values superior to FFFF
            res =  -((65536-res));
            resConverted = mFormat.format(res / 104);// Divido por 104 para pasar la medida a m/s^2
            return resConverted;
        }
        else
            resConverted = mFormat.format(res/104);
        return resConverted;
    }

    public Double Acceleration_Vector(String Ax, String Ay, String Az){//get the total vector from acceleration
        Double ACC = Math.sqrt(Math.pow(Double.parseDouble(Ax),2)+Math.pow(Double.parseDouble(Ay),2)+Math.pow(Double.parseDouble(Az),2));
        return ACC;
    }

    public List<Integer> Binned(Cursor Data ){//funcion para crear senales en bin's

        Log.w(TAG, "MAF OBTENIDO");
        Log.w(TAG, "BINNING  START");

        MovBinned.clear();//clean the list
        for (Data.moveToFirst(); !Data.isAfterLast(); Data.moveToNext()) {//put the cursor into  list
            // The Cursor is now set to the right position
            Double binnedPRE = Double.parseDouble(Data.getString(1));//gets the actual value into a double type
            int binnedPOST = (int) Math.rint(binnedPRE); //turns the value into the closest integer
            MovBinned.add(binnedPOST);
        }

        Integer sizeBinned = Collections.max(MovBinned)-Collections.min(MovBinned);//get the size from the binned signal


        Log.w(TAG, "size of the data is: "+ sizeBinned);
        Log.w(TAG, "BINNING  ACQUIRED");

        return MovBinned;

    }

    public List<Integer> Histogram(List<Integer> Data){//logic for the histogram
        int possibilities = Collections.max(Data);//get the the max value of the data to create an array with that number of values
        int possibilitiesNegative = Collections.min(Data);//get the the max value of the data to create an array with that number of values
        Integer[] HistogramHolder = new Integer[possibilities+1];
        Integer[] HistogramHolderNegative = new Integer[possibilitiesNegative+1];
        Arrays.fill(HistogramHolder, 0);//initialize my array with zeros
        Arrays.fill(HistogramHolderNegative, 0);//initialize my array with zeros
        MovHistogram.clear();//ALWAYS CLEAN THE DATA FOR YOUR LIST'S
        MovHistogramNegative.clear();//ALWAYS CLEAN THE DATA FOR YOUR LIST'S
        for (int i = 0; i < Data.size(); i++) {//put the cursor into  list
            //Log.w(TAG, "histogram numero: "+Data.get(i));
            if (Data.get(i)>=0) {
                HistogramHolder[Data.get(i)] = HistogramHolder[Data.get(i)] + 1;//adds a 1 for each sample
            }

        }
        for (int i1 = 0; i1 < possibilities; i1++){//make the list for containing the values of the histogram
            MovHistogram.add(HistogramHolder[i1]);
        }

        MovHistogram.add(Data.size());
        Log.w(TAG,"size 1 phase: "+Data.size());
        MovHistogram.add(possibilities-possibilitiesNegative);
        Log.w(TAG,"possibilities 1 phase: "+possibilities);
        return MovHistogram;


    }
    public List<Double> Mean(List<String> HistogramHolder){//logic used to the get the mean and SD
        //FIRST VALUE OF THE LIST WILL BE THE MEAN
        //SECOND VALUE OF THE LSIT WILL BE THE SD
        Double mean = 0.0;
        Double variance = 0.0;
        Double StandardDeviation = 0.0;
        ListMeanVar.clear();
        Integer size = HistogramHolder.size();
        Integer Datasize = Integer.parseInt(HistogramHolder.get(size-2));
        Integer possibilities = Integer.parseInt(HistogramHolder.get(size-1));
        for (int i2 = 0; i2 < possibilities; i2++){
            mean = mean + i2*Integer.parseInt(HistogramHolder.get(i2));
        }
        mean = mean/(Datasize);
        ListMeanVar.add(mean);

        for (int i3 = 0; i3 < possibilities; i3++){
            variance = variance+Integer.parseInt(HistogramHolder.get(i3))*Math.pow(i3-mean, 2);
        }
        variance = variance/(Datasize-1);
        StandardDeviation = Math.sqrt(variance);
        ListMeanVar.add(StandardDeviation);

        return ListMeanVar;
    }

    public List<Double> MeanWindow(Cursor data){
        Double Valor = 0.0;//mean
        Double ValorVariaza = 0.0;//SD
        int n = 1;//number of the window
        int valorInicial = 0;
        int WindowSize = 236;//sets the size of the window
        ListMeanWindow.clear();
        for (data.moveToFirst(); !data.isAfterLast(); data.moveToNext()) {//put the cursor into a list
            // The Cursor is now set to the right position
            ListMeanWindow.add(Double.parseDouble(data.getString(1)));
        }
        MeanWindow.clear();
        MeanWindowValue.clear();
        SDWindowValue.clear();
        for(int i = 0; i < ListMeanWindow.size(); i++ ){

            Valor = Valor + ListMeanWindow.get(i);

            if(i >= n*WindowSize){
                MeanWindowDimension.clear();
                for (int i1 = valorInicial; i1 <= i; i1++ ){
                    MeanWindowDimension.add(ListMeanWindow.get(i1));
                }
                MeanWindow.add(MeanWindowDimension);
                Valor = Valor/WindowSize;
                MeanWindowValue.add(Valor);
                for (int i2 = valorInicial; i2 < i; i2++){
                    ValorVariaza = ValorVariaza + Math.pow(ListMeanWindow.get(i2)-Valor,2);
                }
                ValorVariaza = ValorVariaza/WindowSize;
                ValorVariaza = Math.sqrt(ValorVariaza);
                SDWindowValue.add(ValorVariaza);
                Valor = 0.0;
                ValorVariaza = 0.0;
                valorInicial = i;
                n++;
            }

        }
        return MeanWindowValue;
    }




}
