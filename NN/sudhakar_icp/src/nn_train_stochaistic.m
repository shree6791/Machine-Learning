function [CFvalues, inputUnitWeights, hiddenUnitWeights, status, i] = nn_train_stochaistic(Xtrain,Ltrain,model)
%% Varaibles Used 
% C         # of Classes
% D         # of Features
% N         # of Data Samples
% Xtrain    Input Dataset
% Ltrain    Class Labels
%% Initiaize Variables 

% Find # of Data Samples
N = size(Xtrain,1);

% Initailize # of Input and Output Units
inputUnitCount = size(Xtrain,2);
outputUnitCount = size(Ltrain,2);

%% Initialize Neural Network Parameters

tol = model.tol;
maxIter = model.maxIter;
stepSize = model.stepSize;
momentum = model.momentum;
hiddenUnitCount = model.hiddenUnitCount;

%% Add 1s Column to Training Data to support Bias    

Xtrain = [ones(N,1) Xtrain];  

%%  Initialize Weights of Input and Hidden Layers

    minRange = -0.05; maxRange = 0.05;    
    inputUnitWeights = (maxRange-minRange).*rand(inputUnitCount+1,hiddenUnitCount) + minRange;
    hiddenUnitWeights = (maxRange-minRange).*rand(hiddenUnitCount+1,outputUnitCount) + minRange;
    
    previousInputUnitGradient= zeros(inputUnitCount+1,hiddenUnitCount);
    previousHiddenUnitGradient = zeros(hiddenUnitCount+1,outputUnitCount);
    
%   Initialize Array to Store N Cost Function Values
    CFvalues = zeros(maxIter,1);
    
%   Initialize Convergence to Zero
    status = 0;
  
%% Start Back Propagation Algorithm

for i = 1 : maxIter   
    
    for j = 1 : N
    
        % Initialize Current Input and Output
        currentInput = Xtrain(j,:);
        currentOutput = Ltrain(j,:);
        
        % Compute O/p Hidden Layer Units
        hiddenUnitOutput = sigmoid(currentInput,inputUnitWeights);
        
        % Add Bias to Hidden Layer
        hiddenUnitInput = [1 hiddenUnitOutput];
        
        % Compute O/p of Output Layer Units
        predictedOutput = sigmoid(hiddenUnitInput,hiddenUnitWeights);        
        
        % Compute Error in Output Layer 
        outputError = predictedOutput .* (1 - predictedOutput) .* (currentOutput - predictedOutput);
        
        % Compute Weighted Error from Each Output Unit
        temp = zeros(1,hiddenUnitCount);
        for x = 1 : hiddenUnitCount
            temp(x) = sum(hiddenUnitWeights(x+1,:).*outputError);
        end
        
        % Compute Error in Hidden Layer       
        hiddenUnitError = hiddenUnitOutput .* (1 - hiddenUnitOutput) .* temp;
        
        % Compute Gradient of Input and Hidden Layer
        hiddenUnitGradient = (hiddenUnitInput' * outputError) + (momentum * previousHiddenUnitGradient);
        inputUnitGradient =  (currentInput' * hiddenUnitError) + (momentum * previousInputUnitGradient);
        
        % Update Weights of Input and Hidden Unit Layer
        hiddenUnitWeights = hiddenUnitWeights + (stepSize * hiddenUnitGradient);
        inputUnitWeights = inputUnitWeights + (stepSize * inputUnitGradient);
        
        % Update previousHiddenUnitGradient and previousInputUnitGradient
        previousInputUnitGradient = inputUnitGradient;
        previousHiddenUnitGradient = hiddenUnitGradient;
        
        % Compute Cost Function
        CFvalues(i) = CFvalues(i) + costFunction(currentOutput,predictedOutput);
        
    end
    
    % Compute Cost Function
    CFvalues(i) = 1/N * CFvalues(i);
    
    % Check For Convergence
    if(CFvalues(i) < tol)
        status = 1;
        break;
    end
    
end

end