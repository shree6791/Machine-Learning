function predictedOutput = nn_classify(Xdata, inputUnitWeights, hiddenUnitWeights)

%   Find # of Data Samples
N = size(Xdata,1);

%   Add 1s Column to Test Data to Support Bias Element
Xdata = [ones(N,1) Xdata];

% Compute O/p of Hidden Layer Units
hiddenUnitOutput = sigmoid(Xdata,inputUnitWeights);

% Add Bias to Hidden Layer
hiddenUnitInput = [ones(N,1) hiddenUnitOutput];

% Compute O/p of Output Layer Units
predictedOutput = sigmoid(hiddenUnitInput,hiddenUnitWeights);

% Convert Output to form
predictedOutput = predictedOutput >= 0.5;
   

end