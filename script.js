const chatMessages = document.getElementById('chatbox');
const userInput = document.getElementById('userInput');
const sendBtn = document.getElementById('send-btn');
const micBtn = document.getElementById('mic-btn');
const recordingStatus = document.getElementById('voiceStatus');

// Typing indicator functionality
let typingIndicatorObj = null;

// Web Speech API setup
const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
let recognition;

if (SpeechRecognition) {
    recognition = new SpeechRecognition();
    recognition.continuous = false;
    recognition.lang = 'en-US';
    recognition.interimResults = false;
    recognition.maxAlternatives = 1;

    recognition.onstart = function () {
        micBtn.classList.add('active');
        recordingStatus.style.display = 'flex';
    };

    recognition.onresult = function (event) {
        const transcript = event.results[0][0].transcript;
        userInput.value = transcript;
    };

    recognition.onerror = function (event) {
        console.error('Speech recognition error', event.error);
        micBtn.classList.remove('active');
        recordingStatus.style.display = 'none';
        appendMessage('Bot', 'Sorry, I couldn\'t catch that.', 'bot-message');
    };

    recognition.onend = function () {
        micBtn.classList.remove('active');
        recordingStatus.style.display = 'none';

        if (userInput.value.trim() !== "") {
            sendMessage();
        }
    };
} else {
    micBtn.style.display = 'none';
}

micBtn.addEventListener('click', () => {
    if (recognition) {
        if (micBtn.classList.contains('active')) {
            recognition.stop();
        } else {
            recognition.start();
        }
    }
});

function sendSuggestion(text) {
    userInput.value = text;
    sendMessage();
}

function appendMessage(sender, message, className) {
    // If it's a typing indicator, handle differently
    if(className === 'typing') {
        const typingDiv = document.createElement('div');
        typingDiv.className = 'msg-row';
        typingDiv.id = 'typing-div';
        typingDiv.innerHTML = `
            <div class="icon-circle" style="width:35px; height:35px; font-size:0.9rem;"><i class="fas fa-robot"></i></div>
            <div class="dots"><span></span><span></span><span></span></div>
        `;
        // Insert right before the recordingStatus element so it doesn't push below it
        chatMessages.insertBefore(typingDiv, recordingStatus);
        typingIndicatorObj = typingDiv;
        chatMessages.scrollTop = chatMessages.scrollHeight;
        return;
    }

    const messageDiv = document.createElement('div');
    messageDiv.className = className === 'user-message' ? 'msg-row user-row' : 'msg-row';
    
    let iconHtml = className === 'user-message' 
        ? `<div class="icon-circle" style="width:35px; height:35px; font-size:0.9rem; background:linear-gradient(135deg, #1e3c72, #2a5298);"><i class="fas fa-user"></i></div>` 
        : `<div class="icon-circle" style="width:35px; height:35px; font-size:0.9rem;"><i class="fas fa-robot"></i></div>`;
        
    let bubbleClass = className === 'user-message' ? 'msg-bubble user-bubble' : 'msg-bubble';

    messageDiv.innerHTML = `
        ${iconHtml}
        <div class="${bubbleClass}">${message}</div>
    `;

    chatMessages.insertBefore(messageDiv, recordingStatus);
    chatMessages.scrollTop = chatMessages.scrollHeight;
}

function removeTypingIndicator() {
    if(typingIndicatorObj) {
        typingIndicatorObj.remove();
        typingIndicatorObj = null;
    }
}

function fetchChatHistory() {
    fetch('chat?action=history')
        .then(response => response.json())
        .then(data => {
            if (data.history && data.history.length > 0) {
                // Clear any existing Welcome dummy texts before injecting real history
                const previousRows = chatMessages.querySelectorAll('.msg-row');
                previousRows.forEach(r => r.remove());

                data.history.forEach(msg => {
                    const className = msg.sender === 'user' ? 'user-message' : 'bot-message';
                    appendMessage(msg.sender, msg.text, className);
                });
            } else {
                appendMessage('Bot', 'Welcome! Ask your question', 'bot-message');
            }
        })
        .catch(err => {
            console.error('Error fetching history:', err);
            appendMessage('Bot', 'Welcome! Ask your question', 'bot-message');
        });
}

function sendMessage() {
    const text = userInput.value.trim();
    if (text === '') return;

    appendMessage('User', text, 'user-message');
    userInput.value = '';

    appendMessage('Bot', '', 'typing');

    setTimeout(() => {
        fetch('chat', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: `message=${encodeURIComponent(text)}`
        })
            .then(response => response.json())
            .then(data => {
                removeTypingIndicator();
                appendMessage('Bot', data.reply, 'bot-message');
            })
            .catch(error => {
                console.error('Error:', error);
                removeTypingIndicator();
                appendMessage('Bot', 'Error connecting to the server.', 'bot-message');
            });
    }, 1000); 
}

sendBtn.addEventListener('click', sendMessage);

userInput.addEventListener('keypress', (e) => {
    if (e.key === 'Enter') {
        sendMessage();
    }
});

window.onload = fetchChatHistory;
