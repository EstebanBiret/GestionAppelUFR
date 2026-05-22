function renderAbsenceChart(labels, rates) {
    new Chart(document.getElementById("absenceChart"), {
        type: "line",
        data: {
            labels: labels,
            datasets: [{
                label: "Taux d'absence (%)",
                data: rates,
                borderColor: "#C8102E",
                backgroundColor: "rgba(200,16,46,0.08)",
                tension: 0.3,
                fill: true,
                pointBackgroundColor: "#C8102E",
                pointRadius: 4
            }]
        },
        options: {
            plugins: {
                legend: { display: false }
            },
            scales: {
                x: {
                    grid: { color: "#E0DDD8" },
                    ticks: { font: { family: "DM Sans", size: 11 }, color: "#6B6B6B" }
                },
                y: {
                    min: 0,
                    max: 100,
                    grid: { color: "#E0DDD8" },
                    ticks: {
                        callback: v => v + "%",
                        font: { family: "DM Sans", size: 11 },
                        color: "#6B6B6B"
                    }
                }
            }
        }
    });
}