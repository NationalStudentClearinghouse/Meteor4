/*
 * SimpleModal Basic Modal Dialog
 * http://www.ericmmartin.com/projects/simplemodal/
 * http://code.google.com/p/simplemodal/
 *
 * Copyright (c) 2010 Eric Martin - http://ericmmartin.com
 *
 * Licensed under the MIT license:
 *   http://www.opensource.org/licenses/mit-license.php
 *
 * Revision: $Id$
 */
jQuery(function ($) {
	$('.cityLender').click(function (e) {
		$('#showCity').modal({
			maxWidth: 574,
			minHeight: 170
			
		});
		return false;
	});
	$('.Servicing').click(function (e) {
		$('#showServicing').modal({
			maxWidth: 574,
			minHeight: 170
			
		});
		return false;
	});
	$('.PaymentHistory').click(function (e) {
		$('#showHistory').modal({
			maxWidth: 574,
			minHeight: 170
			
		});
		return false;
	});
	$('.OutstandingFees').click(function (e) {
		$('#showOutstanding').modal({
			maxWidth: 574,
			minHeight: 170
			
		});
		return false;
	});
	$('.helpSection').click(function (e) {
		$('#showHelp').modal({
			maxWidth: 800,
			minHeight: 170
			
		});
		return false;
	});
	
	$('.CumulativeDeferment').click(function (e) {
		$('#showCumulative').modal({
			maxWidth: 800,
			minHeight: 400
			
		});
		return false;
	});
	
	
	
	
	
});