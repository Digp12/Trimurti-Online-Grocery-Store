document.addEventListener("DOMContentLoaded", function () {
    const payNowBtn = document.getElementById('payNowBtn');
    const orderForm = document.getElementById('orders');
    const paymentStatusInput = document.getElementById('paymentStatus');
    const placeOrderBtn = document.getElementById('placeOrderBtn');

    $('input[name="paymentType"]').on('change', function () {
        if (this.value === 'ONLINE') {
            $('#payNowBtn').removeClass('d-none');
            $('#placeOrderBtn').addClass('d-none'); 
        } else {
            $('#payNowBtn').addClass('d-none');
            $('#placeOrderBtn').removeClass('d-none');
        }
    });

    payNowBtn.addEventListener('click', function (e) {
        e.preventDefault();

        payNowBtn.disabled = true;
        payNowBtn.textContent = "Processing...";

        const rawAmount = document.getElementById('totalOrderAmount').innerText;
        const cleanedAmount = rawAmount.replace(/[^\d.]/g, '');
        const amountInRupees = parseFloat(cleanedAmount);

        if (isNaN(amountInRupees)) {
            alert("Invalid amount. Please check order total.");
            payNowBtn.disabled = false;
            payNowBtn.textContent = "Pay Now";
            return;
        }

        const amountInPaise = Math.floor(amountInRupees * 100);

        fetch(`http://localhost:8080/api/payments/create-order`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                amount: amountInPaise,
                currency: "INR"
            })
        })
        .then(res => res.json())
        .then(order => {
            const options = {
                key: "rzp_test_1tK7yAY7ByvjWB",
                amount: order.amount,
                currency: order.currency,
                name: "Trimurti-Online Grocery Store",
                description: "Order Payment",
                order_id: order.id,
                handler: function (response) {
                    alert("Payment Successful! Payment ID: " + response.razorpay_payment_id);

                    paymentStatusInput.value = "ONLINE";
                    
                    // Add payment ID to the form
                    const paymentIdInput = document.createElement('input');
                    paymentIdInput.type = 'hidden';
                    paymentIdInput.name = 'paymentId';
                    paymentIdInput.value = response.razorpay_payment_id;
                    orderForm.appendChild(paymentIdInput);

                    payNowBtn.classList.add('d-none');
                    placeOrderBtn.classList.add('d-none');

                    const loader = document.createElement('div');
                    loader.innerHTML = `<div class="text-center mt-3 text-primary">Placing your order... please wait</div>`;
                    orderForm.appendChild(loader);

                    orderForm.submit();
                },
                theme: {
                    color: "#3399cc"
                }
            };

            const rzp = new Razorpay(options);
            rzp.open();
        })
        .catch(err => {
            console.error("Payment Error:", err);
            alert("Payment failed. Please try again.");
            payNowBtn.disabled = false;
            payNowBtn.textContent = "Pay Now";
        });
    });

    orderForm.addEventListener("submit", function (e) {
        const selectedPayment = document.querySelector('input[name="paymentType"]:checked');
        if (selectedPayment && selectedPayment.value === 'COD') {
            paymentStatusInput.value = "COD";

            placeOrderBtn.disabled = true;
            placeOrderBtn.textContent = "Placing Order...";
        }
    });
});
