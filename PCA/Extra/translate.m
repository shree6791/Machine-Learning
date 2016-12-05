function xtrans=translate(x);
%%
% xtrans=translate(x)
% translate the pixels in x by  positions to the rigth
% Input: x: Dx1 matrix, D should be equal to 256.
% Output xtrans: Dx1 matrix: the translated version of x
x=reshape(x,16,16)';
trans=4;
xtrans=zeros(size(x));
xtrans(:,1+trans:end)=x(:,1:end-trans);
xtrans=reshape(xtrans',16*16,1);