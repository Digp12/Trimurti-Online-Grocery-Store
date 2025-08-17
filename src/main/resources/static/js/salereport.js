

let labels = reportData.map(item => item.date);  // Extract the dates
let orderCounts = reportData.map(item => item.totalOrders);  // Extract the total orders
let revenues = reportData.map(item => item.totalRevenue);  // Extract the total revenue

// Get the canvas element where the chart will be drawn
const ctx = document.getElementById('salesChart').getContext('2d');

// Create the chart using Chart.js
const salesChart = new Chart(ctx, {
    type: 'line',  // Type of chart (line chart in this case)
    data: {
        labels: labels,  // The labels (dates)
        datasets: [
            {
                label: 'Total Orders',
                data: orderCounts,  // The order counts for the chart
                borderColor: 'rgba(255, 99, 132, 1)',  // Line color for orders
                backgroundColor: 'rgba(255, 99, 132, 0.2)',  // Background color for the line
                tension: 0.3  // Line tension for smooth curves
            },
            {
                label: 'Revenue (₹)',
                data: revenues,  // The revenue for the chart
                borderColor: 'rgba(54, 162, 235, 1)',  // Line color for revenue
                backgroundColor: 'rgba(54, 162, 235, 0.2)',  // Background color for the line
                tension: 0.3  // Line tension for smooth curves
            }
        ]
    },
    options: {
        responsive: true,  // Make the chart responsive
        scales: {
            y: {
                beginAtZero: true  // Ensure the Y-axis starts from zero
            }
        }
    }
});
