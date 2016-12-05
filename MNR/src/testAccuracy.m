function accuracy = testAccuracy(actualOutput, predictedOutput)
    
    % Function Calculates Accuracy By Finding Correct # of Predictions
    accuracy = sum(actualOutput==predictedOutput)/size(actualOutput,1);
    
end