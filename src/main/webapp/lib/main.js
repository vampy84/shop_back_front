$(function (){

	$.ajax({
		type: 'GET',
		url: '/api/products',
		success: function(products) {

			$.each(products, function(i, product) {

				// Populate the Product page with relevant data

				$("<div class='col-md-4 item text-center'></div>").attr('id', "product" + i).appendTo("#products");
				$("<img class='img-responsive center-block' src='http://placehold.it/350x350'>").appendTo("#product" + i);
				$("<h2></h2>").attr('id', product.name).appendTo("#product" + i).append(product.name);
				$("<p></p>").attr('id', "productDescription").appendTo("#product" + i).append(product.description);
				$("<button type='button' class='btn btn-primary btn-lg' data-toggle='modal'>BUY NOW!</button>").attr('data-target', '#theModal').attr('id', "BuyButton").attr('data-prod', product.id).attr('data-name', product.name).appendTo("#product" + i);

				// Associate Products with Prices
				$.ajax({
					type: 'GET',
					url: '/api/prices',
					success: function(prices) {
						$.each(prices, function(x, price) {
							if(price.id == product.priceId){
								$("<h3></h3>").attr('id', "productPrice").appendTo("#product" + i).append("Price: " + (price.oneTimePrice).toFixed(2));
							}
						});
					}
				});
			});
		}

	});


});

$(document).ready(function() {

$('#theModal').on('show.bs.modal', function (event) {
	var button = $(event.relatedTarget); // Button that triggered the modal
	var prodId = button.data('prod'); // Extract info from data-* attributes
	var name = button.data('name');
	var modal = $(this);
	modal.find('.modal-title').text('Purchase form for ' + name);


	$('#confirmed').on('click', function() {
		//Data collection and POST message for DB

		var formCompanyName = modal.find('#company-name').val();
		var formStreet = modal.find('#street').val();
		var formPostalCode = modal.find('#postal-code').val();
		var formCity = modal.find('#city').val();
		var formCountry = modal.find('#country').val();
		var formProductID = button.data('prod');
		var formProductName = button.data('name');

		$.ajax({
			type: 'POST',
			url: '/api/orders',
			data: {
				companyName: formCompanyName,
				street: formStreet,
				postalCode: formPostalCode,
				city: formCity,
				country: formCountry,
				productID: formProductID,
				productName: formProductName
				},
			contentType: "application/x-www-form-urlencoded",
		});
		// Forced page refresh to reset modal and avoid duplicate submissions on confirmation
		window.location.href = window.location.href;

	});

});

});
