function [Ypred, PCP] = knn_classify(X,Xref,k,p,unknown_label)

% Input
%
%   X               : Test data
%   Xref            : Training data
%   k               : k nearest neighbours
%   p               : Lp norm used
%   unknown_label   : Integer used to label test samples in case of tie
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

%   Find # of Different Classes Present in Training Data Set : Xref
    distinctClassCount = numel(uniqueClassValue);    

%   Initialize Predicted Output Vector : Ypred
    Ypred = zeros(testDataCount,1);
    
%   Initialize PCP Matrix
    PCP = zeros(testDataCount,distinctClassCount);
    
%   Initialize an array to facilitate PCP calculation
    classCount = zeros(distinctClassCount,1);

%% KNN Algorithm

for i = 1 : testDataCount
   
%   Classify ith test sample using KNN

%   Create trainingDataCount * 1 matrix of ith test sample
    currentTestData = repmat(testData(i,:),trainingDataCount,1);
     
%   Initialize Distance vector to find  Distance of ith test sample from 
%   all Training Data samples using Norm P
   
    distance = zeros(trainingDataCount,1);

    for j = 1 : trainingDataCount
        dist = currentTestData(j,:) - trainingData(j,:);
        distance(j) = norm(dist,p);
    end
    
%   Sort distance found in ascending order
    [~,position] = sort(distance);
    
%   Find K Nearest Neighbours of ith test sample for given training samples
    kNearestNeighbours = trainingClass(position(1:k));%,:);    
        
%   Find count of different classes sourrounding given ith test sample    
    for j = 1 : distinctClassCount    
        classCount(j) = sum(uniqueClassValue(j)== kNearestNeighbours);
    end
    
%   Find index of class that is sourrounding ith test sample the most 
     [maxValue, nearestClassIndex] = max(classCount);
     
%   Find Predicted Output for given ith Test Sample
     Ypred(i) = uniqueClassValue(nearestClassIndex);
     
%   Look for repeated value of Ypred(i)
    duplicates = classCount==maxValue;
    duplicateSize = sum(duplicates);
%      
%   If duplicate values exists, replace Ypred as unknown
    if(duplicateSize>1)
        Ypred(i) = unknown_label;
    end
     
%   Calculate PCP
    PCP(i,:) = classCount/k;
    
%   Reinitialize "classCount" Array
%   classCount = zeros(distinctClassCount,1);

end


    
end