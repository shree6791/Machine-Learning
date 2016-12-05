function finalCrossEntropy = crossEntropy(actualOutput,predictedOutput)

finalCrossEntropyTest = -trace(actualOutput'*log(predictedOutput));

% Convert Matrix to Vector Form
actualOutput = actualOutput(:);
predictedOutput = predictedOutput(:);

% Calculate Cross Entropy
crossEntropy = actualOutput.*log(predictedOutput);

% Replace 0.ln(0) {Infinity} Value in Cross Entropy with 0
crossEntropy((crossEntropy==-inf)) = 0;

% Replace NaN Value in Cross Entropy with 0
crossEntropy((isnan(crossEntropy))) = 0;

% Find Average Cross Entropy
finalCrossEntropy = -1 * sum(crossEntropy);

end