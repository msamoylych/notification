const LOGIN_FORM = $('#loginForm');
const USER_NAME = $('#userName');
const LOGOUT_FORM = $('#logoutForm');
const MENU = $('#menu');

function login(evt) {
    let byEvt = evt !== undefined;
    $.ajax({
        method: 'POST',
        url: '/login',
        contentType: 'application/json; charset=UTF-8',
        data: byEvt ? JSON.stringify({login: $('#login').val(), password: $('#password').val()}) : '',
        dataType: 'json',
    }).done(function (result) {
        switch (result.result) {
            case 'ok':
                LOGIN_FORM.removeClass('has-error');
                LOGIN_FORM.hide();
                USER_NAME.text(result.login);
                LOGOUT_FORM.show();
                break;
            case 'bad':
                if (byEvt) {
                    LOGIN_FORM.addClass('has-error');
                }
                LOGIN_FORM.show();
                break;
        }
    })
}

function logout() {
    $.ajax({
        method: 'POST',
        url: '/logout',
        contentType: 'application/json; charset=UTF-8',
        dataType: 'json'
    }).always(function () {
        USER_NAME.text('');
        LOGOUT_FORM.hide();
        LOGIN_FORM.show();
        MENU.empty();
    })
}