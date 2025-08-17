$(function(){
    // Admin: Add Product Validation
    var $addProduct = $("#addProduct");

    $addProduct.validate({
        rules:{
            title:{ required:true, all:true },
            description:{ required:true, all:true },
            category:{ required:true },
            price:{ required:true, number:true, min:1 },
            discount:{ required:true, number:true, min:0, max:100 },
            stock:{ required:true, numericOnly:true, min:0 },
            file:{ required:true, imageType:true }
        },
        messages:{
            title:{ required:'Title required', all:'Invalid title' },
            description:{ required:'Description required', all:'Invalid description' },
            category:{ required:'Please select category' },
            price:{ required:'Price required', number:'Numbers only', min:'Minimum price should be 1' },
            discount:{ required:'Discount required', number:'Numbers only', min:'Minimum discount is 0', max:'Maximum discount is 100' },
            stock:{ required:'Stock required', numericOnly:'Only digits allowed', min:'Stock cannot be negative' },
            file:{ required:'Image required', imageType:'Only jpg, jpeg, png allowed' }
        },
        errorElement: 'span',
        errorPlacement: function(error, element) {
            error.addClass('text-danger');
            error.insertAfter(element);
        }
    });

    // Admin: Edit Product Validation
    var $editProduct = $("#editProduct");

    $editProduct.validate({
        rules:{
            title:{ required:true, all:true },
            description:{ required:true, all:true },
            category:{ required:true },
            price:{ required:true, number:true, min:1 },
            discount:{ required:true, number:true, min:0, max:100 },
            stock:{ required:true, numericOnly:true, min:0 },
            file:{ imageType:true }
        },
        messages:{
            title:{ required:'Title required', all:'Invalid title' },
            description:{ required:'Description required', all:'Invalid description' },
            category:{ required:'Please select category' },
            price:{ required:'Price required', number:'Numbers only', min:'Minimum price should be 1' },
            discount:{ required:'Discount required', number:'Numbers only', min:'Minimum discount is 0', max:'Maximum discount is 100' },
            stock:{ required:'Stock required', numericOnly:'Only digits allowed', min:'Stock cannot be negative' },
            file:{ imageType:'Only jpg, jpeg, png allowed' }
        },
        errorElement: 'span',
        errorPlacement: function(error, element) {
            error.addClass('text-danger');
            error.insertAfter(element);
        }
    });

    // Admin: Add Category Validation
    var $addCategory = $("#addCategory");

    $addCategory.validate({
        rules:{
            categoryName:{ required:true, lettersonly:true }
        },
        messages:{
            categoryName:{ required:'Category name required', lettersonly:'Invalid name' }
        }
    });

    // Admin: Change Order Status Validation
    var $changeOrderStatus = $("#changeOrderStatus");

    $changeOrderStatus.validate({
        rules:{
            status:{ required:true }
        },
        messages:{
            status:{ required:'Please select status' }
        }
    });

    // Admin: Refund Request Validation
    var $refundRequest = $("#refundRequest");

    $refundRequest.validate({
        rules:{
            amount:{ required:true, number:true, min:1 },
            reason:{ required:true, all:true }
        },
        messages:{
            amount:{ required:'Amount required', number:'Numbers only', min:'Min 1' },
            reason:{ required:'Reason required', all:'Invalid text' }
        }
    });

    // Admin: Login Validation
    var $adminLogin = $("#adminLogin");

    $adminLogin.validate({
        rules:{
            username:{ required:true, space:true },
            password:{ required:true, space:true }
        },
        messages:{
            username:{ required:'Username required', space:'No spaces' },
            password:{ required:'Password required', space:'No spaces' }
        }
    });

    // Admin: Add Admin Validation
    var $addAdmin = $("#addAdmin");
    if ($addAdmin.length) {
        $addAdmin.validate({
            rules: {
                name: { required: true, lettersonly: true },
                email: { required: true, space: true, email: true },
                mobileNumber: { required: true, space: true, numericOnly: true, minlength: 10, maxlength: 12 },
                password: { required: true, space: true, passwordStrength: true },
                cpassword: { required: true, space: true, equalTo: '#password' },
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
                cpassword: { required: 'Confirm password', space: 'No spaces', equalTo: 'Passwords do not match' },
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
}); 