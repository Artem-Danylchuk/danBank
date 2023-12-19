

function flipCard() {
    var card = document.getElementById('card');
    card.classList.toggle('flipped');
}

var sumFromTimLeaf = "";
document.querySelector('.card-back p').innerText = sumFromTimLeaf;

function openConfirmation() {
    event.preventDefault();
    document.getElementById('overlay').style.display = 'block';
    document.getElementById('confirmationBox').style.display = 'block';
    console.log('DOM fully loaded. Ready to execute code. openConfirmation');
}

function closeConfirmation() {
    document.getElementById('overlay').style.display = 'none';
    document.getElementById('confirmationBox').style.display = 'none';
    console.log('DOM fully loaded. Ready to execute code. closeConfirmation');
}

function sendData() {
    var selectCard = document.getElementById("clientId").value;
    var sum = document.getElementById("sum").value;
    var lastSearchValue = document.getElementById("searchCardNumber").value;

    console.log('Data to send:', { selectCard, sum, lastSearchValue });

    fetch('http://localhost:7777/search', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ selectCard, sum, lastSearchValue })
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Response from server:', data);
            document.getElementById('response-container').innerText = 'Your status: ' + data.status;
        })
        .catch(error => {
            console.error('Error sending data:', error);
            document.getElementById('response-container').innerText = 'Error: ' + error.message;
        });
}
const myModal = document.getElementById('myModal')
const myInput = document.getElementById('myInput')

myModal.addEventListener('shown.bs.modal', () => {
    myInput.focus()
})








function updateConfirmationBox() {
    // Получаем выбранную карту из <select>
    var selectedCard = document.getElementById("clientId").value;

    // Обновляем соответствующий элемент в блоке подтверждения
    document.getElementById("senderCardConfirmation").innerText = selectedCard;
}







