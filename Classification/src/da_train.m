function [Means, Covariances, Priors] = da_train(Xtrain, Ltrain,classifier_type,model)
%% Initialize DA Parameters

%   Find # of Data Samples in Dataset : Xtrain
    [N, D] = size(Xtrain);

%   Find Different Classes Present in Dataset : Xtrain
    classLabels = unique(Ltrain(:,end));

%   Find # of Different Classes Present in Dataset :  Xtrain
    classCount = numel(classLabels);
    
%   Create a Cell to Store Index Vectors of Each Class  
    indVecCell = cell(classCount,1);

%   Create a Cell to Store Covariance Matrices of Each Class 
    Covariances = cell(classCount,1);

%   Create a Vector to Store Prior values p of Data Samples
    Priors = zeros(classCount,1);

%   Create a Cell to store Mean Vectors
    Means = cell(classCount,1);
    
%% Compute Covariance Matrix of For Different DA Models 

for i = 1 : classCount
    
    % Get indices corresponding to Current Class Label.
    indVecCell{i,1} = Ltrain == classLabels(i);
    
    % Compute the Priors values for ith data.
    Priors(i) = sum(indVecCell{i,1}) / N;
   
    % Find # of Data Present in ith Class Label
    % Nk = sum(indVecCell{i,1});
    
    % Fetch Data Corresponding to ith Class Label
    ithData = Xtrain(indVecCell{i,1},:);
    
    % Initialize "mean" to Find Covariance of Data Corresponding to Class Label
    Means{i,1} = mean(ithData,1)';
    %meanValue = repmat(Means(i,1),Nk,1);
    
    % Compute Covariance of Data Corresponding to Class Label
    Covariances{i,1} = cov(ithData);
    
    if(strcmp(classifier_type,'Naive Bayes'))
        
        % Compute Vkd
        Vkd = sum(Covariances{i,1},2);
        
        % Compute Covariance of Data Corresponding to Class Label
        Covariances{i,1} = diag(Vkd);
        
    elseif(strcmp(classifier_type,'Isotropic'))
        
        % Compute Vkd
        Vk = 1/D*(Covariances{i,1}^.5);
        
        % Compute Covariance of Data Corresponding to Class Label
        Covariances{i,1} = Vk ;
        
    end
    
end

% Since Covaraiance is Same for all Classes, Add Individual Covariance Matrix of Each Class

if (strcmp(model,'LDA'))
    
    Covariances{1,1} = Priors(1)*Covariances{1,1} + Priors(2)*Covariances{2,1} + Priors(3)*Covariances{3,1};
    Covariances{2,1} = Covariances{1,1};
    Covariances{3,1} = Covariances{1,1};
    
end


end


