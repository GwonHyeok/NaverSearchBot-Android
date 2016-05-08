function loadScriptUrl(url, callback) {
    console.log("load script url");
    var head = document.getElementsByTagName('head')[0];
    var script = document.createElement('script');
    script.type = 'text/javascript';
    script.src = url;
    script.onload = callback;
    head.appendChild(script);
}

function onLoadJQuery() {
    console.log('onLoadJQuery()');
    window.$j = jQuery.noConflict();
    HelloNaver.onLoadJQuery();
}

loadScriptUrl('https://ajax.googleapis.com/ajax/libs/jquery/1.12.0/jquery.min.js', onLoadJQuery);


