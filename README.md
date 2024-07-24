# **Smiles Flight Finder**

This application queries the Smiles API to fetch flights based on user-provided parameters through a localhost.

## **How to Use**

1. Run `ProjetoSmilesSearchApplication`.
2. Go to `localhost:8080`.
3. Replace the default values with your desired search parameters.
4. Click on **Buscar** to search for flights.

## **Issues**

- **International Flights:** Queries for flights outside Brazil currently do not work. This will be addressed when time permits.
- **API Stability:** The Smiles API is pretty unstable. If you don't get any results, visit [Smiles](https://www.smiles.com.br/home) and search for flights manually. If the page is blank except for the header, the API is down.
- **Deployment:** Not working properly when deployed.
## **Additional Information**

- **"Valor Total":** Calculated by multiplying the number of miles by 17.5 if below 50,000 miles, or by 17.0 if above 50,000 miles, plus `costTax`. You can adjust this value on line 49 of the `index.html` class.
- **Note:** Values shown are for a single passenger only.
