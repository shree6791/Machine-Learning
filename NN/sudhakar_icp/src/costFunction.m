function costFunctionValue = costFunction(actualOutput,predictedOutput)

    % Compute the Difference in Actual and Predicted Output
    difference = actualOutput(:) - predictedOutput(:);
    
    % Compute Cost Function
    costFunctionValue = 0.5 * sum(difference.^2);

end