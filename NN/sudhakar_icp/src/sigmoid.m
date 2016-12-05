function predictedOutput = sigmoid(dataSample, weights)

% Compute Output of Each Hidden Unit
predictedOutput = dataSample * weights;

% Take Sigmoid of Output to Make Data Linearly Separable
predictedOutput = 1./(1 + exp(-predictedOutput));
     
end