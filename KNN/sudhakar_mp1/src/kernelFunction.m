function [u] = kernelFunction(u,kernel_type)

%  Find total # of data points lying inside kernel of kernel_type
       
if(strcmp('Gaussian',kernel_type))
    
%   Gaussian Kernel Equation 

    numerator = exp(-0.5 * (u.^2));
    denominator = sqrt(2*pi);    
    u = numerator./denominator;
        
elseif (strcmp('Squared Sinc',kernel_type))
    
%   Squared Sinc Kernel Equation 
    
    kernelIndex = u ==0;
    u(kernelIndex) = 1/pi;

    
    kernelIndex = u~=0;    
    temp1 = sin(u(kernelIndex))./u(kernelIndex);
    u(kernelIndex) = (temp1.^2)/pi;
   
    
end

end