function [Ypred,PCP] = pwc_classify(X,Xref,kernel_type,spread,unknown_label)

% Input
%
%   X                 : test data
%   Xref              : training data
%   kernel_type       : 1. Gaussian 2. Squared Sinc
%   spread:           : Spread of Kernel (+ve)
%   unknown_label     : when posterior probability = 0/0, class = unknown
%
% Output
%
%   Ypred           : Predicted Output
%   PCP             : Posterior Class Probabilities of Classes in Xref

%% Initialize Data Set

%   Class Vector
    trainingClass = Xref(:,end);

%   Data Matrix
    testData = X(:,1:end-1);
    trainingData = Xref(:,1:end-1);

%% Initiaize Variables used in KNN Algorithm

%   Find # of Data Samples present in Test and Training Dataset
    testDataCount = size(X,1);
    trainingDataCount = size(Xref,1);

%   Find Different Classes Present in Training Dataset : Xref
    uniqueClassValue = unique(trainingClass);

%   Find # of Different Classes Present in Training Dataset : Xref
    distinctClassCount = numel(uniqueClassValue);    

%   Initialize Predicted Output Vector : Ypred
    Ypred = zeros(testDataCount,1);
    
%   Initialize PCP Matrix
    PCP = zeros(testDataCount,distinctClassCount);
    
%% Parzen Window Algorithm

for i = 1 : testDataCount
   
%   Classify ith test sample using Parzen Window Algorithm

    for  j = 1 : distinctClassCount
        
%       Find Indicies of Training Data containig jth uniqueClassValue
        currentTrainingDataIndex = trainingClass==uniqueClassValue(j);
        
%       Create a Dataset out of Training Data containing jth uniqueClassValue
        currentTrainingData = trainingData(currentTrainingDataIndex,:);
        
%       Find # of Data Samples in currentTrainingData 
        currentTrainingDataCount= size(currentTrainingData,1);
        
%       Create currentTrainingDataCount * 1 matrix of ith test sample
        currentTestData = repmat(testData(i,:),currentTrainingDataCount,1);
    
%       Find Parzen Window consisting of jth uniqueClassValue 
        u = zeros(currentTrainingDataCount,1);

%       Find Normalized Distance of ith Test Sample from Every Sample in Current Dataset  
        for k = 1 : currentTrainingDataCount
            u(k) = norm(currentTestData(k,:) - currentTrainingData(k,:))/spread;
        end
    
%       Find total # of data points lying inside kernel of given kernel_type
        kernel = kernelFunction(u,kernel_type);    
       
%       Conditional Density of jth uniqueClassValue in current Parzen Window 
        PCP(i,j) = sum(kernel)/(spread*currentTrainingDataCount);
        
%       If PCP(i,j) is NaN, replace it with unknown_label
        if (isnan(PCP(i,j)))
            PCP(i,j) = unknown_label;
        end
            
    end
        
end

%% Classify Data Dased on Posterior Class Probability(PCP)
        
%   If PCP of 1st Class Value > PCP of 2nd Class Value, ith Test Data is
%   Classified as Class 1       
    greaterThanIndex = PCP(:,1) > PCP(:,2);
    Ypred(greaterThanIndex) = 1;
                 
%   If PCP of 1st Class Value < PCP of 2nd Class Value, ith Test Data is
%   Classified as Class 2    
    lessThanIndex = PCP(:,1) < PCP(:,2);
    Ypred(lessThanIndex) = 2;
        
%   If PCP of 1st Class Value = PCP of 2nd Class Value, ith Test Data is
%   Classified as unknown_label  
     equalToIndex = PCP(:,1) == PCP(:,2);
     Ypred(equalToIndex) = unknown_label;

    
end
    



