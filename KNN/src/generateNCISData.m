function [X] = generateNCISData(X, pFlip)

% Find # of Data Samples in Given Dataset X
dataCount = size(X,1);

for i = 1 : dataCount

%   Generate Pesudo Random in range 0 - 1 
    p = rand();
    
%   Flip if Class Label of ith Data Sample is to be Flipped
    if(p<pFlip)

%   Flip Class Value of ith Data Sample to it's Complement Form
        
        if(X(i,end)==1)
            X(i,end) = 2;
        else
            X(i,end) = 1;
        end  
        
    end
    
end        
    
end


