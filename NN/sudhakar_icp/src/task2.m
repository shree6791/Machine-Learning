%% Variables Used
% Dataset Parameters
%   D              # of Features
%   N              # of Data Samples
%   K              # of Folds in Cross Validation
%   Xtest          Features of Test Dataset
%   Xtrain         Features of Training Dataset
%   Xvalid         Features of Validation Dataset
%   Ltrain         Labels of Training Dataset
%   Ltest          Labels of Training Dataset
%   Lvalid         Labels of Vaildation Dataset
% Training Paramters
%   Lpred          Predicted Ouptut using LDA/acQDA Classifier
%   Scores         Predicted Posterior Class Probabilities

%% Initialize MATLAB Window
   close all; clear; clc; 

%% Load and Initiaize Data Sets

prompt = 'Choose a Data Set \n1. Iris \n2. Car Evaluation\n';
dataSetOption = input(prompt);

if(dataSetOption == 1)
    load('iris.data.shuffled.mat')
    Pattern = encodeData(iris.Pattern);
    Label = encodeData(iris.Label);
    model.tol = 0.01;
elseif (dataSetOption == 2)
    load('car.data.shuffled.mat')
    Pattern = encodeData(car.Pattern);
    Label = encodeData(car.Label);
    model.tol = 0.002;
else
    disp('Invalid Input, Try Again');
    return
end

%% Initialize Index of Data Sets

N = size(Pattern,1);

trainBegin = 1;
trainEnd = ceil(0.6*N);

validBegin = trainEnd +1;
validEnd = ceil(0.2*N) + trainEnd;

testBegin = validEnd + 1;
testEnd = N;

%% Initialize Neural Network Parameters

model.maxIter = 5000;
model.stepSize = 1;
model.momentum = 0.01;
model.hiddenUnitCount = 4;

%% Commpute K-Fold Cross Validation Error

% Iniitialize Variables
K = 1;
accuracySum = 0;
iterationCount = zeros(K,1);

% Initialize Data Set for Cross Validation
Ldata = Label(trainBegin:validEnd,:);
Xdata = Pattern(trainBegin:validEnd,:);

% Initialize K-Fold Cross Validation Parameters
first = 1;
last = ceil(validEnd/K);
batchSize = last;

for i = 1 : K

% Split Data Set
[newXtrain,newXtest,newLTrain,newLtest] = splitDataSet(first,last,Xdata,Ldata);

% Train Neural Network
[~, inputUnitWeights, hiddenUnitWeights, ~, iterationCount(i)] = nn_train_batch(newXtrain,newLTrain,model);

% Classify Test Data
LtestPred = nn_classify(newXtest,inputUnitWeights,hiddenUnitWeights);

% Compute Accuracy of Classification
accuracySum = accuracySum + testAccuracy(newLtest, LtestPred);

% Make Set for Next Batch
first = first + batchSize;
last = last + batchSize;

if(last>validEnd)
    last = validEnd;
end

end

CV_Error = 100 - (accuracySum / K);
Message = sprintf('%d-Fold Cross Validation Error: %2.2f',K,CV_Error);
fprintf('Batch Gradient Descent\n\n');
disp(Message);

averageIterationCount = ceil(mean(iterationCount));
Message = sprintf('Average Iteration Count Required to Converge : %d \n',averageIterationCount);
disp(Message);

% Set Maxium Iteration Count to Optimal Value Found Using Cross Validation
model.maxIter = averageIterationCount;


%% Initialize Dataset

Xtrain = Pattern(trainBegin:trainEnd,:);
Ltrain = Label(trainBegin:trainEnd,:);

Xvalid = Pattern(validBegin:validEnd,:);
Lvalid = Label(validBegin:validEnd,:);

Xtest = Pattern(testBegin:testEnd,:);
Ltest = Label(testBegin:testEnd,:);

%% Train Neural Network and Verify Results : Batch Gradient Descent

% Train Neural Network
[CEvalues, inputUnitWeights, hiddenUnitWeights, status,~] = nn_train_batch(Xtrain,Ltrain,model);

% Compute Validation Set Accuraccy
LvalidPred = nn_classify(Xvalid,inputUnitWeights,hiddenUnitWeights);
validationSetAccuracy = testAccuracy(Lvalid, LvalidPred);
fprintf('Validation Set Accuracy : %2.2f\n',validationSetAccuracy);

% Compute Test Set Accuracy
LtestPred = nn_classify(Xtest,inputUnitWeights,hiddenUnitWeights);   
testSetAccuracy = testAccuracy(Ltest, LtestPred);
fprintf('Test Set Accuracy : %2.2f\n',testSetAccuracy);

% Display Convergence of Data
trainingConvergence(status);

%% Plot Graphs of Cost Function vs Iteration Count

figure('Name','Batch Gradient Descent');

plot(CEvalues)
xlabel('Iteration Count');
ylabel('Cost Function');
title(sprintf('Cost Function\nStep Size = %1.2f\nMomentum = %1.3f',model.stepSize,model.momentum));
