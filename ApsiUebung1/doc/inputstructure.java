for (int i = 0; i < input.length; i++) out[i] = input[i];
if (r > 0)                             out[input.length] = -128; 
for (int i = 1; i < r; i++)            out[input.length + i] = 0;
for (int i = 0; i < 8; i++)            out[out.length - 8 + i] = length[i];