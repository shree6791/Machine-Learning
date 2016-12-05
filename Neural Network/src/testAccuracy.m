function accuracy = testAccuracy(actualOutput, predictedOutput)
    
    % Find # of Data Samples
    N = size(actualOutput,1);
    
    % Find # of Classes
    C = size(actualOutput,2);
    
    % Find if Actual and Predicted Output are Equal 
    compare = actualOutput == predictedOutput;
    
    %Find # of Correct Prediction for Each Data Sample
    compareSum = sum(compare,2);
    
    % Compute Accuracy of Neural Network
    accuracy = sum(compareSum == C) * 100 / N;
    
end