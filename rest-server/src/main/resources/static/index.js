let id, gameState;

const N = 11;
const icons = {};
let paths = Array(N).fill(null).map(() => Array(N).fill(null));

const stateLabel = $('#gameState');
let canvas, ctx;

initIcons();
connect();

function paintBoard() {
    stateLabel.text(normalizeStr(gameState['gameState']));
    const colors = ['#FFCE9E', '#E8AB6F', '#D18B47'];
    let size = updateSize();
    let selectedI = gameState['selectedCoordinate']['i'];
    let selectedJ = gameState['selectedCoordinate']['j'];
    let half = N / 2 | 0;
    let count = 200;
    for (let r = -half; r <= half; r++) {
        for (let q = -half; q <= half; q++) {
            count--;
            if (Math.abs(r + q) > half) continue;
            let i = r + half;
            let j = q + Math.min(0, r) + half;
            let centerX = Math.round(size * (1.5 * q + half * 1.9));
            let centerY = Math.round(size * (Math.sqrt(3) * (r + 0.5 * q) + half * 1.9));
            paths[i][j] = new Path2D();
            for (let k = 0; k < 6; k++) {
                paths[i][j].lineTo(centerX + Math.round(size * Math.cos(k * 2 * Math.PI / 6)),
                    centerY + Math.round(size * Math.sin(k * 2 * Math.PI / 6)));
            }
            let cell = gameState['board'][i][j];
            if (selectedI === i && selectedJ === j) {
                ctx.fillStyle = '#ffff00';
            } else if (cell['capturable'] === true) {
                ctx.fillStyle = '#ff0000';
            } else {
                ctx.fillStyle = colors[count % 3];
            }
            ctx.fill(paths[i][j]);
            if (cell['reachable'] === true) {
                paintReachable(centerX, centerY, size);
            }
            if (cell['piece'] != null) {
                ctx.drawImage(icons[cell['piece']['color'].toLowerCase() + cell['piece']['type']],
                    centerX - size / 2, centerY - size / 2, size, size);
            }
        }
    }
}

function paintReachable(centerX, centerY, size) {
    ctx.fillStyle = '#ffff00';
    ctx.beginPath();
    ctx.arc(centerX, centerY, size / 4, 0, 2 * Math.PI);
    ctx.closePath();
    ctx.fill();
}

function updateSize() {
    let size = (Math.min($(window).width(), $(window).height()) / (2 * N)) | 0;
    canvas.width = size * N * Math.sqrt(3);
    canvas.height = size * N * Math.sqrt(3);
    return size;
}

function normalizeStr(str) {
    return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

function connect() {
    $.ajax({
        url: 'api/logic', type: 'POST', dataType: 'text'
    }).done((data) => {
        id = data;
        $(window).on('unload', () => {
            navigator.sendBeacon(`api/logic/${id}`);
        });
        updateGameState();
    }).fail(() => location.reload());
}

function initCanvas() {
    $('#loadingImg').remove();
    $('#leftPanel').append('<canvas width="1280px" height="720px" id="canvas"></canvas>');
    canvas = document.getElementById('canvas');
    ctx = canvas.getContext('2d');
    $(canvas).click(canvasClick);

    if (gameState['nowTurn'] === 'WHITE') {
        $('#whiteLabel').css('border-color', 'darkred');
    } else {
        $('#blackLabel').css('border-color', 'darkred');
    }
    $(window).resize(paintBoard);
    setInterval(() => updateGameState(), 1000);
}

function canvasClick(e) {
    for (let i = 0; i < N; i++) {
        for (let j = 0; j < N; j++) {
            if (paths[i][j] != null && ctx.isPointInPath(paths[i][j], e.offsetX, e.offsetY)) {
                sendClick(i, j);
                return;
            }
        }
    }
}

function updateGameState() {
    $.get(`api/logic/${id}`).done((data) => {
        if (Object.keys(data).length !== 0) {
            gameState = data;
            if (canvas == null) {
                initCanvas();
            }
            paintBoard();
        } else {
            setTimeout(updateGameState, 1000);
        }
    }).fail(() => handleFail());
}

function sendClick(i, j) {
    let half = N / 2 | 0;
    $.ajax({
        url: `api/select/${id}`,
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({
            'i': i, 'j': j,
            'r': i - half,
            'q': j - half - Math.min(0, i - half)
        })
    }).done((response) => {
        if (response) {
            let piece = choosePiece();
            promotePawn(piece);
        } else {
            updateGameState();
        }
    }).fail(() => handleFail());
}

function promotePawn(piece) {
    $.ajax({
        url: `api/promote/${id}`,
        type: 'PUT',
        contentType: 'application/json',
        data: JSON.stringify({
            'type': piece,
            'color': gameState['nowTurn']
        }),
    }).done(() => updateGameState())
        .fail(() => handleFail());
}

function handleFail() {
    location.reload();
    alert("Second player has left");
}

function choosePiece() {
    let piece;
    do {
        piece = prompt('Queen, Knight, Rook, Bishop?');
        if (piece != null) {
            piece = piece.trim().toLowerCase();
        }
    } while (piece !== 'queen' && piece !== 'knight' && piece !== 'rook' && piece !== 'bishop');
    return piece;
}

function initIcons() {
    let src = ['icons/WhiteKing.png', 'icons/BlackKing.png', 'icons/WhitePawn.png', 'icons/BlackPawn.png', 'icons/WhiteQueen.png', 'icons/BlackQueen.png', 'icons/WhiteKnight.png', 'icons/BlackKnight.png', 'icons/WhiteBishop.png', 'icons/BlackBishop.png', 'icons/WhiteRook.png', 'icons/BlackRook.png'];

    let keys = ['whiteking', 'blackking', 'whitepawn', 'blackpawn', 'whitequeen', 'blackqueen', 'whiteknight', 'blackknight', 'whitebishop', 'blackbishop', 'whiterook', 'blackrook'];

    for (let i = 0; i < 12; i++) {
        let image = new Image();
        image.src = src[i];
        icons[keys[i]] = image;
    }
}