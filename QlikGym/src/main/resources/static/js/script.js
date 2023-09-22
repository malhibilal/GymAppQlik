const toggleSidebar = () => {
  if ($(".sidebar").is(":visible")) {
    //true
    //band karna hai
    $(".sidebar").css("display", "none");
    $(".content").css("margin-left", "0%");
  } else {
    //false
    //show karna hai
    $(".sidebar").css("display", "block");
    $(".content").css("margin-left", "20%");
  }
};

// search functionality
const search = () => {
  //console.log("searching");
  let query=$("#search-input").val();
  console.log(query);
  if(query == ''){
    $(".search-result").hide();

  }else{
    console.log(query);
    // sending requet to the server
    let url = `http://localhost:8084/search/${query}`;
    fetch(url)
    .then((response) => {
      return response.json();
    })
    .then((data) => {
      console.log(data);
      let text = `<div class="list-group">`

      data.forEach((contact) => {
        text += `<a href='/admin/${trainer.id}/trainer'
        class='list-group-item list-group-item-action'> ${trainer.name}  </a>`;
      });

      text+= `</div>`;
      $(".search-result").html(text);
      $(".search-result").show();

    });
  }
};