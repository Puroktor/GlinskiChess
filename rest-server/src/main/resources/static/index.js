const N = 11;
const colors = ['#FFCE9E', '#E8AB6F', '#D18B47'];

const canvas = document.getElementById('canvas');
const ctx = canvas.getContext("2d");

paintBoard();
$(window).resize(paintBoard);

function paintBoard(){
	let size = (Math.min($(window).width(), $(window).height()) / (2*N))|0;
	canvas.width = size * N * Math.sqrt(3);
	canvas.height = size * N * Math.sqrt(3);
	let half = N/2|0;
	let count = 200;
	for (let r = -half; r <= half; r++) {
			for (let q = -half; q <= half; q++) {
				count--;
				if (Math.abs(r + q) > half)
					continue;
				let centerX = Math.round(size * (1.5 * q + half * 1.9));
				let centerY = Math.round(size * (Math.sqrt(3) * (r + 0.5 * q) + half * 1.9));
				let path = new Path2D();
				for (let k = 0; k < 6; k++) {
					path.lineTo(centerX + Math.round(size * Math.cos(k * 2 * Math.PI / 6)),
							centerY + Math.round(size * Math.sin(k * 2 * Math.PI / 6)));
				}
				ctx.fillStyle = colors[count%3];
				ctx.fill(path);
			}
	}
}

$('#canvas').click(function(e) {
});