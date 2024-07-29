document.getElementById("searchButton").addEventListener("click", function() {
    const departureCode = document.querySelector(".departureAirPort").value;
    const arrivalCode = document.querySelector(".arrivalAirPort").value;
    const flightDate = document.querySelector(".departureDate").value;
    const numberPassenger = document.querySelector(".passengerAmount").value;


    const apiUrl = 'https://<SEU_BACKEND_PRODUCAO>/api/search';
    fetch(`/api/search?originAirportCode=${departureCode}&arrivalAirportCode=${arrivalCode}&departureDate=${flightDate}&adults=${numberPassenger}`)
        .then(response => response.json())  // Assume JSON response
        .then(data => {
            const resultsContainer = document.getElementById("results");
            resultsContainer.innerHTML = '';  // Limpa o conteúdo anterior

            if (Array.isArray(data) && data.length > 0) {
                const ul = document.createElement('ul');
                data.forEach(flight => {
                    const li = document.createElement('li');

                    // Calcula o valor total
                    const miles = flight.miles;
                    const costTax = flight.costTax;
                    let valueInReais;

                    // Calcula o valor com base em milhares de milhas
                    const milesInThousands = miles / 1000;

                    if (milesInThousands <= 50) {
                        valueInReais = milesInThousands * 17.5;
                    } else {
                        valueInReais = milesInThousands * 17;
                    }

                    valueInReais += costTax;  // Adiciona a taxa ao valor calculado

                    li.innerHTML = `${flight.departureAirportCode} ${flight.departureTime} -> ${flight.arrivalAirportCode} ${flight.arrivalTime} <br> Milhas: ${flight.miles} <br> Taxa: ${flight.costTax.toFixed(2)} <br> Valor Total: R$${valueInReais.toFixed(2)}`;
                    ul.appendChild(li);
                });
                resultsContainer.appendChild(ul);
            } else {
                resultsContainer.textContent = 'Nenhum voo encontrado.';
            }
        })
        .catch(error => console.error('Erro:', error));
});
