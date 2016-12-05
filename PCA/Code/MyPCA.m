function [eigenValue,eigenVector,meanX] = MyPCA(x,eigenFacesCount)
%%
% Performs the extraction of the PCA components given a dataset 
% Input:    X, DxN matrix of N points x of dimension D 
% Output:   
%    Lambda : set of eigenvalues of the covariance matrix ranked in decreasing order
%    U      : matrix of eigenvectors (ranked in the same order than eigenvalues)
%    meanX  : average value of the datas in X
% 

   [N,D] = size(x); 
   meanX = mean(x);
   meanX = repmat(meanX,N,1);
   covariance = 1/N *((x - meanX)' * (x - meanX));
   
   [eigenVector, eigenValue] = eigs(covariance,eigenFacesCount);    
   eigenValue = diag(eigenValue);
   
% %% Find Sufficient Number of Features Needed
%    
%    eigenValue = diag(eigenValue);
%    eigenValueSum = sum(eigenValue);
%    eigenValueThresholdSum = 0.9 * eigenValueSum;
%    
%    i = 1;
%    tempSum = eigenValueSum;
%    eigenValue = sort(eigenValue);
%    
%    
%    while(tempSum > eigenValueThresholdSum)       
%        tempSum = tempSum - eigenValue(i,1);
%        i = i + 1;       
%    end
%        
%    eigenVector = eigenVector (:,1:i-1);
%    
% end