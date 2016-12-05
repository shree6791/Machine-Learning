%% Initialise Work Space

close all;
clc; clear;

%% Variables Used in Task 5

% k         K Neighbour Count
% pFlip     Probability of Flip 
% N         # of Data Samples in Dataset 

%   Initialize Dataset Generation Variables
    pFlip = 0.1;
    
%   Initialize KNN Parameters
    norm_type = 2;  
    maxKNeighbours = 15;
    unknown_label = strjoin({'-'});
    kNeighbourValue = 1 : maxKNeighbours ;    
    
%   Initialize Error Parameters
    trainError = zeros(maxKNeighbours,1); 
    validationError = zeros(maxKNeighbours,1); 
        
%   Intitialize Various Dataset Count
    testDataCount = 100;
    trainingDataCount = 30;
    validationDataCount = 100;
    
%%  Create Training Data
    
%   Generate CIS Data
    xTrain = generateCISData(trainingDataCount);

%   Generate NCIS Data
    xTrain = generateNCISData(xTrain,pFlip);
    
%%  Create Validation Data

%   Generate CIS Data
    xValid = generateCISData(validationDataCount);

%   Generate NCIS Data
    xValid = generateNCISData(xValid,pFlip);
    
%%  Create Validation Data

%   Generate CIS Data
    xTest = generateCISData(testDataCount);

%   Generate NCIS Data
    xTest = generateNCISData(xTest,pFlip);    
    
%% Start Task 5 : Find Champion k-NN Model    
    
for i = 1 : maxKNeighbours
    
%   Run KNN Algorithm to Classify Data
    [Ypred_Validation, PCP_Validation] = knn_classify(xValid,xTrain,kNeighbourValue(i),norm_type,unknown_label);
    [Ypred_Train, PCP_Train] = knn_classify(xTrain,xTrain,kNeighbourValue(i),norm_type,unknown_label);
        
%   Find # of Correct Classifications
    correctCountValidation = sum(Ypred_Validation == xValid(:,end));    
    correctCountTrain = sum(Ypred_Train == xTrain(:,end));
    
%   Find # of Wrong Classifications
    wrongCountValidation = sum(Ypred_Validation ~= xValid(:,end));    
    wrongCountTrain = sum(Ypred_Train ~= xTrain(:,end));
    
%   Find Mis-Classification Error Rate
    validationError(i) = 1 - (correctCountValidation/testDataCount);
    trainError(i) = 1 - (correctCountTrain/testDataCount);
    
end

%%   Find Champion k-NN model

%   Large Confidence Interval
    [minError, kNeighbourIndexMin] = min(validationError);

%   Small Confidence Interval
    [maxError, kNeighbourIndexMax] = max(validationError);
    
%%  Plot Graph to find Champion k-NN Model

    figure
    plot(kNeighbourValue,validationError,'r');
    hold on    
    plot(kNeighbourValue,trainError,'b');
    
    xlabel('K Neighbour Count');
    ylabel('Mis-classification error');    
    legend('Validation Error', 'Training Error');
    title(sprintf('Champion k-NN Model is K = %d, Error = %.2f \n Bad KNN Model is K = %d, Error = %.2f ',...
        kNeighbourIndexMin,minError,kNeighbourIndexMax,maxError));
    
%%  Start Task 5 : Find Champion Parzen Window Model   

%   Initialize Parzen Window Parameters
    kernel_type = 'Gaussian';
    spreadValue = 0.001 : 0.05 : 0.5;    
    spreadValueLength = length(spreadValue);
    
%   Re-Initialize Error Parameters
    trainError = zeros(1,spreadValueLength);
    validationError = zeros(1,spreadValueLength);

for i = 1 : spreadValueLength
    
%   Run Parzen Window Algorithm
    [Ypred_Validation,PCP_Validation] = pwc_classify(xValid,xTrain,kernel_type,spreadValue(i),unknown_label);
    [Ypred_Train, PCP_Train] = pwc_classify(xTrain,xTrain,kernel_type,spreadValue(i),unknown_label);
    
%   Find # of Correct Classifications
    correctCountValidation = sum(Ypred_Validation == xValid(:,end));    
    correctCountTrain = sum(Ypred_Train == xTrain(:,end));
    
%   Find # of Wrong Classifications
    wrongCountValidation = sum(Ypred_Validation ~= xValid(:,end));    
    wrongCountTrain = sum(Ypred_Train ~= xTrain(:,end));
    
%   Find Mis-Classification Error Rate
    validationError(i) = 1 - (correctCountValidation/testDataCount);
    trainError(i) = 1 - (correctCountTrain/testDataCount);

    
end

%%   Find Champion Parzen Window Model 

%   Large Confidence Interval
    [minError, spreadIndexMin] = min(validationError);

%   Small Confidence Interval
    [maxError, spreadIndexMax] = max(validationError);
    
%%  Plot Graph to find Champion Parzen Window Model 

    figure
    plot(spreadValue,validationError,'r');
    hold on    
    plot(spreadValue,trainError,'b');
    
    xlabel('spread');
    ylabel('Mis-classification error');    
    legend('Validation Error', 'Training Error');
    title(sprintf('Champion PWC Model is Spread = %.3f, Error = %.2f \n Bad PWC Model is Spread = %.3f, Error = %.2f ',...
        spreadValue(spreadIndexMin),minError,spreadValue(spreadIndexMax),maxError));
    
