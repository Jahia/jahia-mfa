
$(document).ready(function () {
    $('.digit-group').find('input').each(function () {
        $(this).attr('maxlength', 1);
        $(this).on('keyup', function (e) {
            var parent = $($(this).parent());

            if (e.keyCode === 8 || e.keyCode === 37) {
                var prev = parent.find('input#' + $(this).data('previous'));

                if (prev.length) {
                    $(prev).select();
                }
            } else if ((e.keyCode >= 48 && e.keyCode <= 57) || (e.keyCode >= 65 && e.keyCode <= 90) || (e.keyCode >= 96 && e.keyCode <= 105) || e.keyCode === 39) {
                var next = parent.find('input#' + $(this).data('next'));

                if (next.length) {
                    $(next).select();
                } else {
                    if (parent.data('autosubmit')) {
                        parent.submit();
                    }
                }
            }
        });
    });

    $("input[type='text'][name='username']").focusout(function() {
        var sitekey = $(this).attr('sitekey');
        var graphQLQuery = `
        query{
              mfa{
                verifyMFAEnforcement(username:"`+$(this).val()+`",sitekey:"`+sitekey+`")
              }
            }
        `;
        var xhr = new XMLHttpRequest();
        xhr.responseType = 'json';
        xhr.open("POST", "/modules/graphql");
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.setRequestHeader("Accept", "application/json");
        xhr.onload = function () {
            console.log('data returned:', xhr.response.data.mfa.verifyMFAEnforcement);
            if(xhr.response.data.mfa.verifyMFAEnforcement){
                $("#mfa-field").show();
            }else{
                $("#mfa-field").hide();
            }
        }
        xhr.send(JSON.stringify({query: graphQLQuery}));
    });

});