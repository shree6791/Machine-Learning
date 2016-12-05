function [Weights, CEvalues, status] = mnr_train(Xtrain,Ltrain,maxIter,stepSize, beta, tol)
%% Varaibles Used 

% Weights   MNR weights 
% D         # of Features
% X         Training Dataset
% N         # of Data Samples

%% Initiaize Variables Used in Multinomial Regression Algorithm

%   Initialize N,D    
    [N,D] = size(Xtrain); 
    
%   Add 1s Column to Training Data to support Bias    
    Xtrain = [ones(N,1) Xtrain];

%   Find Different Classes Present in Training Dataset
    classLabel = unique(Ltrain);

%   Find # of Different Classes Present in Training Dataset
    classCount = numel(classLabel);        
   
%   Initialize Weight Values b/w -0.05 and 0.05
    Weights = rand(D+1,classCount);
    
%   Form 1 out of C label Encoding for Each Input Sample
    actualOutput = zeros(N,classCount);

    for i = 1 : classCount
        actualOutput(:,i) = classLabel(i) == Ltrain;
    end
  
%   Initialize Array to Store N Cross Entropy Values
    CEvalues = zeros(maxIter,1);
    
%   Initialize Convergence to Zero
    status = 0;
  
%% Start Multinomial Regression Algorithm

for i = 1 : maxIter   
       
    % Compute Posterior Class Probability Estimates
    predictedOutput = softmax(Xtrain,Weights);
    
    % Compute Cross Entropy
    CEvalues(i) = crossEntropy(actualOutput,predictedOutput);
        
    % Compute Gradient
    gradient = Xtrain' * (predictedOutput - actualOutput);
        
    % Calculate Step Size Using Back Tracking Line Search Algorithm
    stepSize = lineSearch(beta,stepSize,Xtrain,Weights,gradient,actualOutput);

    % Update Weights
    Weights = Weights - (stepSize * gradient);
 
    % Compute Log 10 Base L Inf norm of Gradient
    error = log10(norm(gradient(:),inf));
    
    % Check For Convergence
    if(error < tol)
        status = 1;
        break;
    end

end


end