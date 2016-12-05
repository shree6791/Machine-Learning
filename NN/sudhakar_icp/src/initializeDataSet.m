function [X,L] = initializeDataSet()

x = [1 2 3];

load('iris.data.shuffled.mat')
Label = encodedOutput(Label);

C = size(Label,2);

X = cell(3,1);
L = cell(3,1);
start = 1;


K = 50;
last = K;

for i = 1 : 3
    
    X{i,1} = Pattern(start:last,:);    
    L{i,1} = Label(start:last,:);
    
    start = start + K;
    last = K + last;

end

for i = 1 : C
    for j = 1 : C
       x = mod((i+j),C);
       if x == 0
           x = C;
       end
       fprintf('%d',x);
    end
    fprintf('\n');
end

end