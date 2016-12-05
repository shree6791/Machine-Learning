% prompt = 'What is the original value? ';
 %x = input(prompt);
 
 function Xdata = encodeData(Input)
  
 Xdata = [];
 [N,D] = size(Input);
 
 for i = 1 : D
     
     columnValues = unique(Input(:,i));
     columnValuesCount = numel(columnValues);
     
     if (columnValuesCount > 10)
         Xdata = [Xdata Input(:,i)]; %#ok<AGROW>
     else
         for j = 1 : columnValuesCount
             Xdata = [Xdata strcmp(Input(:,i),columnValues(j,1))]; %#ok<AGROW>
         end
     end
     
 end
 
 %Xdata = cell2table(Xdata);
 
 end