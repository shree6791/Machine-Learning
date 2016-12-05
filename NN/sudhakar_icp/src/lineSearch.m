function stepSize = lineSearch(beta,stepSize,Xdata,inputUnitWeights, hiddenUnitWeights,inputUnitGradient,hiddenUnitGradient, actualOutput)
%%  Backtacking Line Search Algorithm

%   Let
%       lhs = f(x - stepSize * gradient)
%       rhs = f(x)

%   Here
%       initialCostFunction = f(x)


%% Initialize Variables

N = size(Xdata,1);
hiddenUnitOutput = sigmoid(Xdata,inputUnitWeights);
hiddenUnitInput = [ones(N,1) hiddenUnitOutput];
initialPredictedOutput = sigmoid(hiddenUnitInput,hiddenUnitWeights);
initialCostFunction = costFunction(actualOutput,initialPredictedOutput);

%% Initial Computation 

updatedInputUnitWeights = inputUnitWeights - (stepSize * inputUnitGradient);
updatedHiddenUnitWeights = hiddenUnitWeights - (stepSize * hiddenUnitGradient);

hiddenUnitOutputLHS = sigmoid(Xdata,updatedInputUnitWeights);
hiddenUnitInputLHS = [ones(N,1) hiddenUnitOutputLHS];
predictedOutputLHS = sigmoid(hiddenUnitInputLHS,updatedHiddenUnitWeights);
lhs = costFunction(actualOutput,predictedOutputLHS);

% Commpute RHS of Algorithm
rhs = initialCostFunction;
    
%%  Execute Algorithm   
    
while (lhs > rhs)
    
    % Decrement Step Size by factor "beta"
    stepSize = beta * stepSize;
    
    % Compute LHS of Algorithm
    updatedInputUnitWeights = inputUnitWeights - (stepSize * inputUnitGradient);
    updatedHiddenUnitWeights = hiddenUnitWeights - (stepSize * hiddenUnitGradient);

    hiddenUnitOutputLHS = sigmoid(Xdata,updatedInputUnitWeights);
    hiddenUnitInputLHS = [ones(N,1) hiddenUnitOutputLHS];
    predictedOutputLHS = sigmoid(hiddenUnitInputLHS,updatedHiddenUnitWeights);
    lhs = costFunction(actualOutput,predictedOutputLHS);
    
    % Commpute RHS of Algorithm
    rhs = initialCostFunction;
    
end
    
end