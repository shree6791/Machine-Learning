function Lpred = encodedOutput(actualOutput)

%   Initialize N,D    
    N = size(actualOutput,1); 

%   Find Different Classes Present in Training Dataset
    classLabel = unique(actualOutput);

%   Find # of Different Classes Present in Training Dataset
    classCount = numel(classLabel);    

    Lpred = zeros(N,classCount);

    for i = 1 : classCount
        Lpred(:,i) = strcmp(classLabel(i),actualOutput);
    end


end