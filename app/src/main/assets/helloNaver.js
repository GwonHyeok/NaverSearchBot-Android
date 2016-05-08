(function () {
    $j(document).ready(function() {

         window.helloNaver = function() {
            return 'Fuck';
        }

        window.searchKeyword = function(keyword, mainTTL) {
            setTimeout(function() {
                console.log("search : " + keyword);
                $j('.sch_inp').val(keyword);
                $j('.sch_submit').click();
            }, mainTTL);
        }

        window.scrollToMoreButton = function() {
            console.log('Scroll To More Button');
            $j('html, body').animate({
                scrollTop: $j('.sp_total')[1].offsetTop
            }, 2000);
        }

        window.touchMoreButton = function (moreTTL) {
            setTimeout(function() {
                console.log('Touch Scroll To More Button');
                $j(".sp_total .more")[1].click()
            }, moreTTL)
        }

        window.hasFindUrl = function(url) {
            console.log("Find Keyword......");

            var isHasKeywordPage = false;
            var allLinkBox = $j('li.api_bx a');
            for(var i=0; i<allLinkBox.length; i++) {
                if ($j(allLinkBox[i]).prop('href') != null) {
                    isHasKeywordPage |= ($j(allLinkBox[i]).prop('href').indexOf(url) != -1); 
                    console.log(isHasKeywordPage);
                }
            }

            return isHasKeywordPage ? true : false;
        }

        window.goNextPage = function(findTTL) {
            if($j($j('.pg2b_btn')[1]).hasClass('dim')) return false;

            setTimeout(function() {
                $j('html, body').animate({
                    scrollTop: $j('.paging_total').offset().top
                }, 2000, function() {
                    // End Scroll Down
                    $j($j('.pg2b_btn')[1]).click();
                });
            }, findTTL);

            return true;
        }

        window.goTargetLink = function(url) {
            var allLinkBox = $j('li.api_bx a');
            for(var i=0; i<allLinkBox.length; i++) {
                if ($j(allLinkBox[i]).prop('href') != null) {

                    // URL 검증 필요하다면 contains와 urlencode 형식으로도 검사
                    if($j(allLinkBox[i]).prop('href').indexOf(url) != -1) {
                        allLinkBox[i].click();
                    }
                }
            }
        }

        window.touchUnified = function(unifiedTTL) {
            console.log("touchUnified!!");
            setTimeout(function() {
                var unifiedList = $j('.lst_sch li a');
                for(var i=0; i<unifiedList.length; i++) {
                    if ($j(unifiedList[i]).prop('href') != null) {
                        if($j(unifiedList[i]).prop('href').indexOf('where=m&') > -1) {
                            console.log('Unified Click');
                            unifiedList[i].click();
                        }
                    }
                }
            }, unifiedTTL);
        }

        window.touchSearch = function(searchTTL) {
            setTimeout(function() {
                $j('.sch_submit').click();
            }, searchTTL);
        }

        window.touchBlog = function(blogTTL) {
            setTimeout(function() {
                var unifiedList = $j('.lst_sch li a');
                for(var i=0; i<unifiedList.length; i++) {
                    if ($j(unifiedList[i]).prop('href') != null) {
                        if($j(unifiedList[i]).prop('href').indexOf('where=m_blog&') > -1) {
                            console.log('Blog Click');
                            unifiedList[i].click();
                        }
                    }
                }
            }, blogTTL);
        }

        window.touchCafe = function(cafeTTL) {
            setTimeout(function() {
            var unifiedList = $j('.lst_sch li a');
                for(var i=0; i<unifiedList.length; i++) {
                    if ($j(unifiedList[i]).prop('href') != null) {
                        if($j(unifiedList[i]).prop('href').indexOf('where=m_cafe&') > -1) {
                            console.log('Cafe Click');
                            unifiedList[i].click();
                        }
                    }
                }
            },cafeTTL);
        }

        window.scrollToBottom = function(stayTTL, urlCheck) {
            if($j == null) return false;

            if(window.location.href.indexOf(urlCheck) > -1) {
                $j('html, body').animate({
                    scrollTop: $j(document).height()
                }, stayTTL);
                return true;
            }

            return false;
        }
    });

    return 'Load Finish';
})();

console.log('Load Naver');