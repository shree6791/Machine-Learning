%% Variables Used
% Dataset Variables
%   Xtest          Features of Test Dataset
%   Xtrain         Features of Training Dataset
%   Ltrain         Labels of Training Dataset
%   Ltest          Labels of Training Dataset
%   IrisReduced    Features 3 and 4 of Dataset
% Training Parameters
%   maxIter         Maximum # of Iterations
%   status          Flag to Indicate Convergence
%   tol             Base-10 Log value of Threshold Error
%   beta            Ratio by which the step length is reduced for each failed backtracking step
% Output Paramters 
%   Weights       Weight values for each class arranged in rows
%   CEValues      Cross-entropy values of model for each Gradient Descent Iteration

%% Initialize MATLAB Window
close all; clear; clc; 

%% Load and Initiaize Data Sets

dataSamples = load('iris.data.shuffled.mat');
Ltrain = dataSamples.Label(1:50,:);
Xtrain = dataSamples.Pattern(1:50,:);

%% Initialize Parameters of Multinomial Regression

tol = 0.02;
beta = 0.5;
maxIter = 1000;
stepSize = 0.5;
stepSizeArray = [0.5 3];

%% Initialize Decision Region Variables

% Find Different Classes Present in Dataset : X
uniqueClassValues = unique(Ltrain(:,end));

% Find # of Different Classes Present in Dataset X
distinctClassCount = numel(uniqueClassValues);

% Assign 3 Colors to Plot 3 Different Labels
uniqueColor = [0 0 1; 1 0 0; 0 1 0];

%% Plot Graphs Against Different Features

figure('Name','Iris Data Set')

% Feature Plot Indicies
differentCombinationsCount = 6;
differentCombinations = [1 2; 1 3; 1 4; 2 3; 2 4; 3 4];

for i = 1 : differentCombinationsCount
    
    subplot(3,2,i)
    c1 = differentCombinations(i,1);
    c2 = differentCombinations(i,2);
        
    for k = 1 : distinctClassCount
        
        % Find Logical Indices of Data Samples whose Class Label = lth Class Value
        currentTrueIndex = uniqueClassValues(k)==Ltrain;
        
        % Store Data Samples whose Class Label = lth Class Value in currentClass
        currentClass = Xtrain(currentTrueIndex,[c1 c2]);
        
        % Plot Graph of Current Class
        plot(currentClass(:,1),currentClass(:,2),'*','color',uniqueColor(k,:))
        
        hold on
        
    end
    
% Label Graph
ylabel(sprintf('Iris Feature %d',c1));
xlabel(sprintf('Iris Feature %d',c2));
legend('Class 1','Class 2', 'Class 3');
    
end

%% Initialize Reduced Iris Data Set to Perform MNR Classification

IrisReduced = dataSamples.Pattern(:,3:4);

Xtrain = IrisReduced(1:50,:);
Ltrain = dataSamples.Label(1:50,:);

Xtest = IrisReduced(51:150,:);
Ltest = dataSamples.Label(51:150,:);

%% Initialize Loop Variables

accuracySum = 0;
[N,D] = size(Xtrain);
   
%%   Find Average LOOC Error of MNR Model
  
for k = 1 : N

    % Split Data Set to form New Test and Training Dataset
    [newXtrain,newXtest,newLTrain,newLtest] = splitDataSet(k,Xtrain,Ltrain);
    
    % Train Model Using Training Data
    [Weights, ~, ~] = mnr_train(newXtrain,newLTrain,maxIter,stepSize,beta,tol);
    
    % Classify Validation Data
    [Lpred, ~] = mnr_classify(newXtest, Weights);
    
    % Calculate Accuracy of Model
    accuracy = testAccuracy(Lpred, newLtest);
    
    % Calculate Sum of Accuracy of Model
    accuracySum =  accuracySum + accuracy;

end

% Calculate Average Error of Current Classifier Model
LOOC = 1 - (accuracySum/N);
       
% Display Avg LOOC Error
fprintf('Average LOOC Error of MNR model is %2.2f\n', LOOC);

% Commpute and Display Test Set Error
[Lpred, Scores] = mnr_classify(Xtest, Weights);
testSetError = 1 - testAccuracy(Ltest, Lpred);
fprintf('Test Set Error of MNR model is %2.2f\n', testSetError);
        
%% Plot Cross-Entropy vs GD Iteration Count

figure('Name','Cross Entropy')
stepSizeArrayCount = size(stepSizeArray,2);

for i = 1 : stepSizeArrayCount

    subplot(1,2,i)
    
    % Train Model Using Training Data
    [~, CEvalues, ~] = mnr_train(Xtrain, Ltrain,maxIter,stepSizeArray(i),beta,tol);
    
    % Plot Graph
    plot(CEvalues)
    ylabel('Cross Entropy');
    xlabel('Iteration Count');
    title(sprintf('Step Size = %1.1f \n', stepSizeArray(i)));
    
end
    
%%  Plot Iris Training Data Set Used in MNR     

figure('Name','Decision Boundary')

for k = 1 : distinctClassCount
    
    % Find Logical Indices of Data Samples whose Class Label = lth Class Value
    currentTrueIndex = uniqueClassValues(k)==Ltrain;
    
    % Store Data Samples whose Class Label = lth Class Value in currentClass
    currentClass = Xtrain(currentTrueIndex,1:2);
    
    % Plot Graph of Current Class
    plot(currentClass(:,1),currentClass(:,2),'*','color',uniqueColor(k,:))
    
    hold on
    
end

%%  Create Artificial Data Set to Train MNR Model
    
% Train Model Using Training Data
[Weights, ~, status] = mnr_train(Xtrain,Ltrain,maxIter,stepSize,beta,tol);

% Display If Training Converged
trainingConvergence(status)

%% Create Artificial Test Data
[x1, x2] = meshgrid(min(Xtest(:,1)):0.01:max(Xtest(:,1)),min(Xtest(:,2)):0.01:max(Xtest(:,2)));
XtestGrid = [x1(:) x2(:)];

% Run MNR Algorithm to Classify Data
[Ypred, ~] = mnr_classify(XtestGrid, Weights);

% Reshape Ypred Values
Ypred = reshape(Ypred,size(x1,1),size(x1,2));
    
%% Plot Decision Regions

contour(x1,x2,Ypred,'Color','b');

% Label Graph
ylabel('Iris Feature 4');
xlabel('Iris Feature 3');
legend('Class 1','Class 2', 'Class 3');
title(sprintf('Decision Boundary for MNR\n\tStep Size : %1.2f',stepSize));    