%% Initialise Work Space

close all;
clc; clear;

%% Variables Used in Task 3

% a                     super-script power
% pFlip                 Probability of Flip
% N                     # of Samples to Draw
% repeatKNN             # of Times KNN is Run  
% error                 N*11 matrix to store error values
% unknown_label         Label Value assigned when there is a Tie   

%%   Initialize Dataset Generation Variables

    N = 10;
    pFlip = 0.1;
    
%   Initialize KNN Parameters
    norm_type = 2;    
    kNeighbourValue = [1 5] ;
    unknown_label = strjoin({'-'});
    
%   Initialize Training Set Size    
    a = 1;
    trainingSetCount = 11;
    trainingPatternCount = zeros(1,trainingSetCount);
    
    for i = 1 : trainingSetCount
        trainingPatternCount(i) = floor(10^a);
        a = a + 0.2;
    end
        
%   Initialize Task Parameters  
    repeatKNN = 30;                                             
    kNeighbourLength = length(kNeighbourValue);
    trainingPatternLength = length(trainingPatternCount);
    
%   Create Test Dataset
    testDataCount = 100;
    xTest = generateCISData(testDataCount);
    
%   Generate NCIS Test Dataset
    xTest = generateNCISData(xTest,pFlip);    
   
%% Start Task 3

%   Plot Box Plot for Different Value of kNeighbour Count

for i = 1 : kNeighbourLength
    
%   Find Error for j Different Training Data Set Size
%   Open Figure Window to plot Box Plot
    figure

%   Initialize 'error' variable 
    error = zeros(repeatKNN,trainingSetCount);    
    
    for j = 1 : trainingSetCount
        
        trainingDataCount = trainingPatternCount(j);
        
    %   Find Error 30 Times for N Different Training Data Combination
        
        for k = 1 : repeatKNN
            
        %   Generate Random CIS Data of size = trainingDataCount
            xTrain = generateCISData(trainingDataCount);
        
        %   Generate Random NCIS Data using above CIS Data
            xTrain = generateNCISData(xTrain,pFlip);
             
        %   Run KNN Algorithm to Classify Data
            [Ypred, PCP] = knn_classify(xTest,xTrain,kNeighbourValue(i),norm_type,unknown_label);
                        
        %   Find # of Correct Classifications
            correctCount = sum(Ypred == xTest(:,end));
            
        %   Find Mis-Classification Error Rate
            error(k,j) = 1 - (correctCount/testDataCount);
            
        end
        
    end
    
    %% Plot Box Plot
    
    boxplot(error, log10(trainingPatternCount));
    %set(gca,'XScale','log')
    
    %   Label the Graph
    ylabel('Mis-Classification Error')
    xlabel('Training Set Size [log 10 Scale]')
    title(sprintf('KNN Algorithm with K = %d',kNeighbourValue(i)));
    
    
end