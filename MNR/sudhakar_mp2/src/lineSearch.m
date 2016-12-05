function stepSize = lineSearch(beta,stepSize,Xdata,Weights,gradient,actualOutput)
%%  Backtacking Line Search Algorithm

%   Let
%       lhs = f(x - stepSize * gradient)
%       rhs = f(x)

%   Here
%       initialCrossEntropy = f(x)

%% Initialize Variables

initialPredictedOutput = softmax(Xdata,Weights);
initialCrossEntropy = crossEntropy(actualOutput,initialPredictedOutput);
    
%% Initial Computation 

% Compute LHS of Algorithm
updatedWeights = Weights - (stepSize * gradient);
predictedOutputLHS = softmax(Xdata,updatedWeights);
lhs = crossEntropy(actualOutput,predictedOutputLHS);

% Commpute RHS of Algorithm
rhs = initialCrossEntropy;
    
%%  Execute Algorithm   
    
while (lhs > rhs)
    
    % Decrement Step Size by factor "beta"
    stepSize = beta * stepSize;
    
    % Compute LHS of Algorithm
    updatedWeights = Weights - (stepSize * gradient);
    predictedOutputLHS = softmax(Xdata,updatedWeights);
    lhs = crossEntropy(actualOutput,predictedOutputLHS);
    
    % Commpute RHS of Algorithm
    rhs = initialCrossEntropy;
    
end
    
end