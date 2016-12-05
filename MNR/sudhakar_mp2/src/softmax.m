function predictedOutput = softmax(Xdata,Weights)

% Variables Used
%   C = # of Classes

% Compute Class Count
C = size(Weights,2);

% Compute Exponential of Predicted Ouptut
numerator = exp(Xdata * Weights);

% Compute Exponential Sum of Predicted Ouptut
numeratorSum = sum(numerator,2);
denominator = repmat(numeratorSum,1,C);

% Compute Predicted Output Using Soft Max
predictedOutput = numerator./denominator;
     
end