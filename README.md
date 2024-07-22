# **Smiles flight finder**
Queries the Smiles API to fetch flights based on the parameters provided by the user through the localhost.



**How to use**\
1 - Run ProjetoSmilesSearchApplication\
2 - go to localhost:8080\
3 - replace the default values\
4 - buscar\



**Issues**\
Apparently queries for flights outside Brazil dont work, might take a look at that later if i manage to get some freetime.

-----------\
"Valor Total" is decided by multiplying the amount of miles by 17.5 if bellow 50.000 and by 17.0 if greater than 50.000, plus costTax, you can change this to the value you want on line 49 on the index.html class.\
Values shown are for a single passenger only.
