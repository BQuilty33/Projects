package com.example.autotrackerca400;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.SparseInstance;
import weka.core.converters.CSVLoader;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
/**
 * <h1>Following Functionality;</h1>
 * <p>
 * - Predicts driver type based on sensor values.
 * </p><p>
 * - Either aggressive,normal or slow.
 *</p>
 */
public class DrivingTypeModel
{

    public Instances filterDataset(InputStream DrivingTypeDatasetCsv) throws Exception {
        // load data from CSV
        CSVLoader loaderCsv = new CSVLoader();
        loaderCsv.setSource(DrivingTypeDatasetCsv);
        // get driving type dataset from csv
        Instances drivingTypeDataset = loaderCsv.getDataSet();
        Remove removeTimeStamp = new Remove();
        // all index for attributes to keep
        int[] indexToKeep = new int[]{0,1,2,3,4,5,6};
        removeTimeStamp.setAttributeIndicesArray(indexToKeep);
        removeTimeStamp.setInvertSelection(true);
        removeTimeStamp.setInputFormat(drivingTypeDataset);
        // Create filter dataset + create class index (slow, normal etc).
        Instances filteredDrivingTypeDataset = Filter.useFilter(drivingTypeDataset, removeTimeStamp);
        filteredDrivingTypeDataset.setClassIndex(filteredDrivingTypeDataset.numAttributes() - 1);
        return filteredDrivingTypeDataset;
    }

    public String[] predictDriverType(ArrayList<String[]> DriverValuesTmp,Classifier DrivingTypeDecisionTreeTMP,Instances FilteredDrivingTypeDatasetTMP ) throws Exception {
        Instance[] driverValuesInstanceTMP = new SparseInstance[DriverValuesTmp.size()];
        String[] predictDriverTypeTMP = new String[DriverValuesTmp.size()];
        int i = 0;
        while (i < DriverValuesTmp.size()){
            String[] tmpDriverValues = DriverValuesTmp.get(i);
            driverValuesInstanceTMP[i] = new SparseInstance(5);
            driverValuesInstanceTMP[i].setDataset(FilteredDrivingTypeDatasetTMP);
            int ii = 0;
            while (ii < tmpDriverValues.length){
                driverValuesInstanceTMP[i].setValue(ii,Double.valueOf(tmpDriverValues[ii]));
                ii += 1;
            }
            double classIndex = DrivingTypeDecisionTreeTMP.classifyInstance(driverValuesInstanceTMP[i]);
            predictDriverTypeTMP[i] = FilteredDrivingTypeDatasetTMP.classAttribute().value((int)classIndex);
            i += 1;
        }
        return predictDriverTypeTMP;
    }

    public int[] getPercentageArray(String[] PredictDriverType, int PreSlow,int PreNormal, int PreAggressive, int PreTotal){
        int[] percentageArray = new int[8];
        percentageArray[0] = Collections.frequency(Arrays.asList(PredictDriverType),"SLOW");
        percentageArray[1] = Collections.frequency(Arrays.asList(PredictDriverType),"NORMAL");
        percentageArray[2] = Collections.frequency(Arrays.asList(PredictDriverType),"AGGRESSIVE");
        percentageArray[3] = PredictDriverType.length;
        percentageArray[4] = Collections.frequency(Arrays.asList(PredictDriverType),"SLOW") + PreSlow;
        percentageArray[5] = Collections.frequency(Arrays.asList(PredictDriverType),"NORMAL") + PreNormal;
        percentageArray[6] = Collections.frequency(Arrays.asList(PredictDriverType),"AGGRESSIVE") + PreAggressive;
        percentageArray[7] = PredictDriverType.length + PreTotal;
        return percentageArray;
    }

    public int[] PredictDriverType(InputStream DrivingTypeDatasetCsv, ArrayList<String[]> DriverValues,int PreSlow,int PreNormal,int PreAggressive,int PreTotal) throws Exception {
        // load data from CSV
        Instances filteredDrivingTypeDataset = filterDataset(DrivingTypeDatasetCsv);

        //build a J48 decision tree
        Classifier drivingTypeDecisionTree = new RandomForest();
        drivingTypeDecisionTree.buildClassifier(filteredDrivingTypeDataset);

        // predict driver type based on driver values
        String[] predictDriverType = predictDriverType(DriverValues,drivingTypeDecisionTree, filteredDrivingTypeDataset);

        // get percentages array of driver type counts
        int[] percentageArray;
        percentageArray = getPercentageArray(predictDriverType,PreSlow,PreNormal,PreAggressive,PreTotal);

        return percentageArray;
    }

}
