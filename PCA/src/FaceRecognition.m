%% Load Given Datasets

clc; clear; close all;

testDataSet = load('Subset3YaleFaces.mat');
trainingDataSet = load('Subset1YaleFaces.mat');
validationDataSet = load('Subset2YaleFaces.mat');

%% MY PCA : Covariance Matrix

eigenFacesCount = 10;
[eigenValue,eigenVector,meanX] = MyPCA(trainingDataSet.X,eigenFacesCount);

%% Projection of Data Set onto Principal Components

projectedTrainingDataSet = PCAProjection(trainingDataSet.X,meanX,eigenVector);

%% Reconstruction of Projected Images

reconstructedTrainingDataSet = PCAReconstruction(eigenVector,meanX,projectedTrainingDataSet);

%% Plot First 9 Eigen Faces

figure

for i = 1 : 9
       
    eFace = reshape(eigenVector(:,i),50,50);        
    
    subplot(3,3,i);
    imagesc(eFace)
    colormap gray
    title(sprintf('Eigen Face %i',i));
    
end

%% Plot First 2 Original and Reconstructed Dataset Samples

figure

for i = 1 : 2
    
subplot(2,2,i);

oFace = reshape(trainingDataSet.X(i,:),50,50);        
imagesc(oFace)
colormap gray
title(sprintf('Data Sample %i',i));    
end
    
for i = 1 : 2
    
subplot(2,2,(i+2));
rFace = reshape(reconstructedTrainingDataSet(i,:),50,50);        
imagesc(rFace)
colormap gray
title(sprintf('Reconstructed Sample %i',i));  

end

%% Plot Mean Face

figure

mFace = reshape(meanX(i,:),50,50);        
imagesc(mFace)
colormap gray
title(sprintf('Mean Face'));

%% Distance between first 2 Dataset Sample in Original D Dimensions
oDiff = norm(trainingDataSet.X(1,:)- trainingDataSet.X(2,:));

%% Distance between first 2 Dataset Sample in Reduced D Dimensions (Principal Components)
pDiff = norm(projectedTrainingDataSet(1,:)- projectedTrainingDataSet(2,:));

%% Distance between first 2 Reconstructed Dataset Sample in Original D Dimensions
rDiff = norm(reconstructedTrainingDataSet(1,:)- reconstructedTrainingDataSet(2,:));

%% KNN Classification for Validation Set

projectedValidationDataSet = PCAProjection(validationDataSet.X,meanX,eigenVector);
CLASS =  knnclassify(projectedValidationDataSet,projectedTrainingDataSet,trainingDataSet.Y,7);

pred = validationDataSet.Y == CLASS;
pos = sum (pred == 1);
neg = sum (pred == 0);
accuracyValidation = pos / (pos + neg);

%% KNN Classification for Test Set

projectedTestDataSet = PCAProjection(testDataSet.X,meanX,eigenVector);
CLASS =  knnclassify(projectedTestDataSet,projectedTrainingDataSet,trainingDataSet.Y,7);

pred = testDataSet.Y == CLASS;
pos = sum (pred == 1);
neg = sum (pred == 0);
accuracyTest = pos / (pos + neg);