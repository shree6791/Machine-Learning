%% Variables Used
% Dataset Parameters
%   D              # of Features
%   N              # of Data Samples
%   Xtest          Features of Test Dataset
%   Xtrain         Features of Training Dataset
%   Ltrain         Labels of Training Dataset
%   Ltest          Labels of Training Dataset
%   IrisReduced    Features 3 and 4 of Dataset
% Training Paramters
%   model          Type of Model Used
%   classifier     Type of Classification 
%   Means          Estimated Means in rows, row k contains mean class k
%   Lpred          Predicted Ouptut using LDA/acQDA Classifier
%   Covariances    Estimated Covariance Matrix of K classes 
%   Scores         Predicted Posterior Class Probabilities
%   Priors         Estimated Class Prior

%% Initialize MATLAB Window
   close all; clear; clc; 

%% Load and Initiaize Data Sets

    dataSamples = load('iris.data.shuffled.mat');     
    IrisReduced = dataSamples.Pattern(:,3:4);

    Xtrain = IrisReduced(1:50,:);
    Ltrain = dataSamples.Label(1:50,:);

    Xtest = IrisReduced(51:150,:);
    Ltest = dataSamples.Label(51:150,:);

    model = {'LDA', 'QDA'};
    classifier = {'General Case', 'Naive Bayes', 'Isotropic'};   
    
%% Initialize Loop Variables

    [N,D] = size(Xtrain);
    trainingModelSize = size(model,2);
    classifierSize = size(classifier,2);
    
    Message = sprintf('Model\t\t\t\tClassifier\t Avg LOOC Error\tTest Set Error');        
    disp(Message)
    
%% Average Error of LDA Models

for i = 1 : trainingModelSize
    
    for j = 1 : classifierSize
        
        avgerageAccuracy = zeros(N,1);    
        
        for k = 1 : N
            
            % Split Data Set to form New Test and Training Dataset
            [newXtrain,newXtest,newLTrain,newLtest] = splitDataSet(k,Xtrain,Ltrain);
            
            % Train Model Using Training Data
            [Means, Covariances, Priors] = da_train(newXtrain, newLTrain,classifier(j),model(i));
            
            % Classify Validation Data
            [Lpred, ~] = da_classify(newXtest, Means,Covariances, Priors);
            
            % Calculate Accuracy of Model
            accuracy = testAccuracy(newLtest,Lpred);
            
            % Calculate Error of Model
            avgerageAccuracy(k) =  avgerageAccuracy(k) + accuracy;
            
        end
        
        % Calculate Average LOOC Error of Current Classifier Model
        LOOC = 1 - mean(avgerageAccuracy);
        
        %% Calculate Test Set Error of Current Classifier Model
        
        % Classify Validation Data
        [Lpred, Scores] = da_classify(Xtest, Means,Covariances, Priors);
        
        % Calculate Accuracy of Model
        testSetError = 1 - testAccuracy(Ltest, Lpred);
        
        %% Display The Errors Calculated
           
        % Display Average LOOC Error Found
        if(strcmp(classifier{1,j},'General Case'))            
            Message = sprintf('%s\t\t\t%s\t\t\t\t%3.2f\t\t%3.2f', classifier{1,j},model{1,i},LOOC,testSetError);
        else
            Message = sprintf('%s\t\t\t\t%s\t\t\t\t%3.2f\t\t%3.2f', classifier{1,j},model{1,i},LOOC,testSetError);
        end
        disp(Message)
        
    end
    
end

%%  Initialize Decision Region Variables
    
%   Find Different Classes Present in Dataset : X
    classLabel = unique(Ltrain(:,end));

%   Find # of Different Classes Present in Dataset X
    classCount = numel(classLabel);

%   Assign 3 Colors to Plot 3 Different Labels
    uniqueColor = [0 0 1; 1 0 0; 0 1 0];

%% Plot Decision Region for LDA and QDA Models
  
for i = 1 : trainingModelSize
    
    modelName = sprintf('%s',model{i});
    figure('Name',modelName)
    
    for j = 1 : classifierSize
        
        subplot(1,3,j)
        
        % Plot Training Data
        
        for k = 1 : classCount
            
            % Find Logical Indices of Data Samples whose Class Label = lth Class Value
            currentTrueIndex = classLabel(k)==Ltrain;
            
            % Store Data Samples whose Class Label = lth Class Value in currentClass
            currentClass = Xtrain(currentTrueIndex,1:2);
            
            % Plot Graph of currentClass
            plot(currentClass(:,1),currentClass(:,2),'*','color',uniqueColor(k,:))
            
            hold on
            
        end
        
        %%  Create Artificial Data Set to Train MNR Model
        
        % Train MNR Model Using Training Data
        [Means, Covariances, Priors] = da_train(Xtrain, Ltrain,classifier(j),model(i));
        
        % Create Artificial Test Data
        [x1,x2] = meshgrid(min(Xtrain(:,1)):0.03:max(Xtest(:,1)),min(Xtest(:,2)):0.03:max(Xtrain(:,2)));
        xTest = [x1(:) x2(:)];
        
        % Run MNR Algorithm to Classify Data
        [Ypred, ~] = da_classify(xTest, Means,Covariances, Priors);
        
        % Reshape Ypred Values
        Ypred = reshape(Ypred,size(x1,1),size(x1,2));
        
        %% Plot Decision Regions
        
        contour(x1,x2,Ypred);
        
        % Label Graph
        ylabel('Iris Feature 4');
        xlabel('Iris Feature 3');
        legend('Class 1','Class 2', 'Class 3');
        title(sprintf('Decision Boundary for %s %s ', classifier{j},model{i}));
        
    end
    
end



