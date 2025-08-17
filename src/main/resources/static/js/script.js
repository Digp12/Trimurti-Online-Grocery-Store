$(function(){
    // Initialize Bootstrap alerts
    var alertList = document.querySelectorAll('.alert');
    alertList.forEach(function(alert) {
        new bootstrap.Alert(alert);
    });

    // Auto-dismiss alerts after 5 seconds
    setTimeout(function() {
        alertList.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Form validation code
    var $userRegister = $("#userRegister");
    if ($userRegister.length) {
        $userRegister.validate({
            rules: {
                name: { required: true, lettersonly: true },
                email: { required: true, space: true, email: true },
                mobileNumber: { required: true, space: true, numericOnly: true, minlength: 10, maxlength: 12 },
                password: { required: true, space: true, passwordStrength: true },
                confirmpassword: { required: true, space: true, equalTo: '#password' },
                address: { required: true, all: true },
                city: { required: true, space: true },
                state: { required: true },
                pincode: { required: true, space: true, numericOnly: true },
                img: { required: true, imageType: true }
            },
            messages: {
                name: { required: 'Name required', lettersonly: 'Invalid name' },
                email: { required: 'Email required', space: 'No spaces allowed', email: 'Invalid email' },
                mobileNumber: { required: 'Mobile no required', space: 'No spaces', numericOnly: 'Invalid number', minlength: 'Min 10 digits', maxlength: 'Max 12 digits' },
                password: { required: 'Password required', space: 'No spaces', passwordStrength: 'Min 8 chars, 1 uppercase, 1 number, 1 special char' },
                confirmpassword: { required: 'Confirm password', space: 'No spaces', equalTo: 'Passwords do not match' },
                address: { required: 'Address required', all: 'Invalid address' },
                city: { required: 'City required', space: 'No spaces' },
                state: { required: 'State required' },
                pincode: { required: 'Pincode required', space: 'No spaces', numericOnly: 'Invalid pincode' },
                img: { required: 'Image required', imageType: 'Only jpg, jpeg, png allowed' }
            },
            errorElement: 'span',
            errorPlacement: function(error, element) {
                error.addClass('text-danger');
                error.insertAfter(element);
            }
        });
    }

    // Custom validation methods
    $.validator.addMethod('lettersonly', function(value, element) {
        return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
    });

    $.validator.addMethod('space', function(value, element) {
        return /^[^-\s]+$/.test(value);
    });

    $.validator.addMethod('all', function(value, element) {
        return /^[^-\s][a-zA-Z0-9_,.\s-]+$/.test(value);
    });

    $.validator.addMethod('numericOnly', function(value, element) {
        return /^[0-9]+$/.test(value);
    });

    $.validator.addMethod('imageType', function(value, element) {
        return this.optional(element) || /\.(jpg|jpeg|png)$/i.test(value);
    });

    $.validator.addMethod('passwordStrength', function(value, element) {
        return /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/.test(value);
    });

    // Orders validation
    var $orders = $("#orders");

    $orders.validate({
        rules:{
            firstName:{ required:true, lettersonly:true },
            lastName:{ required:true, lettersonly:true },
            email:{ required:true, space:true, email:true },
            mobileNo:{ required:true, space:true, numericOnly:true, minlength:10, maxlength:12 },
            address:{ required:true, all:true },
            city:{ required:true, space:true },
            state:{ required:true },
            pincode:{ required:true, space:true, numericOnly:true },
            paymentType:{ required:true }
        },
        messages:{
            firstName:{ required:'First name required', lettersonly:'Invalid' },
            lastName:{ required:'Last name required', lettersonly:'Invalid' },
            email:{ required:'Email required', space:'No spaces', email:'Invalid' },
            mobileNo:{ required:'Mobile required', space:'No spaces', numericOnly:'Invalid', minlength:'Min 10 digits', maxlength:'Max 12 digits' },
            address:{ required:'Address required', all:'Invalid' },
            city:{ required:'City required', space:'No spaces' },
            state:{ required:'State required' },
            pincode:{ required:'Pincode required', space:'No spaces', numericOnly:'Invalid' },
            paymentType:{ required:'Select payment type' }
        }
    });

    // Reset Password Validation
    var $resetPassword = $("#resetPassword");

    $resetPassword.validate({
        rules:{
            password:{ required:true, space:true, passwordStrength:true },
            confirmPassword:{ required:true, space:true, equalTo:'#pass' }
        },
        messages:{
            password:{ required:'Password required', space:'No spaces', passwordStrength:'Min 8 chars, 1 uppercase, 1 number, 1 special char' },
            confirmPassword:{ required:'Confirm password', space:'No spaces', equalTo:'Passwords do not match' }
        }
    });
});
