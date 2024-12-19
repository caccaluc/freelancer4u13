<script>
  import axios from "axios";
  import { page } from "$app/stores";
  import { onMount } from "svelte";
  import { jwt_token } from "../../store";

  const api_root = $page.url.origin;

  let currentPage;
  let nrOfPages = 0;
  let defaultPageSize = 20;

  let companies = [];
  let company = {
    name: null,
    email: null,
  };

  $: {
    if ($jwt_token !== "") {
      let searchParams = $page.url.searchParams;

      if (searchParams.has("page")) {
        currentPage = searchParams.get("page");
      } else {
        currentPage = "1";
      }
      getCompanies();
    }
  }

  /*   onMount(() => {
    getCompanies();
  }); */

  function getCompanies() {
    let query = "?pageSize=" + defaultPageSize + "&pageNumber=" + currentPage;

    var config = {
      method: "get",
      url: api_root + "/api/company" + query,
      headers: { Authorization: "Bearer " + $jwt_token },
    };

    axios(config)
      .then(function (response) {
        companies = response.data.content;
        nrOfPages = response.data.totalPages;
      })
      .catch(function (error) {
        alert("Could not get companies");
        console.log(error);
      });
  }

  function validateEmailAndcreateCompany() {
    var config = {
      method: "get",
      url: "https://disify.com/api/email/" + company.email,
    };

    axios(config)
      .then(function (response) {
        console.log("Validated email " + company.email);
        console.log(response.data);
        if (
          response.data.format &&
          !response.data.disposable &&
          response.data.dns
        ) {
          createCompany();
        } else {
          alert("Email " + company.email + " is not valid.");
        }
      })
      .catch(function (error) {
        alert("Could not validate email");
        console.log(error);
      });
  }

  function createCompany() {
    var config = {
      method: "post",
      url: api_root + "/api/company",
      headers: {
        "Content-Type": "application/json",
        Authorization: "Bearer " + $jwt_token,
      },
      data: company,
    };

    axios(config)
      .then(function (response) {
        alert("Company created");
        getCompanies();
      })
      .catch(function (error) {
        alert("Could not create Company");
        console.log(error);
      });
  }
</script>

<h1 class="mt-3">Create Company</h1>
<form class="mb-5">
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="name">Name</label>
      <input
        bind:value={company.name}
        class="form-control"
        id="name"
        type="text"
      />
    </div>
  </div>
  <div class="row mb-3">
    <div class="col">
      <label class="form-label" for="email">E-Mail</label>
      <input
        bind:value={company.email}
        class="form-control"
        id="email"
        type="email"
      />
    </div>
  </div>
  <button
    type="button"
    class="btn btn-primary"
    on:click={validateEmailAndcreateCompany}>Submit</button
  >
</form>

<h1>All Companies</h1>
<table class="table">
  <thead>
    <tr>
      <th scope="col">Name</th>
      <th scope="col">E-Mail</th>
      <th scope="col">ID</th>
    </tr>
  </thead>
  <tbody>
    {#each companies as company}
      <tr>
        <td>{company.name}</td>
        <td>{company.email}</td>
        <td>{company.id}</td>
      </tr>
    {/each}
  </tbody>
</table>
<nav>
  <ul class="pagination">
    {#each Array(nrOfPages) as _, i}
      <li class="page-item">
        <a
          class="page-link"
          class:active={currentPage == i + 1}
          href={"/companies?page=" + (i + 1)}
          >{i + 1}
        </a>
      </li>
    {/each}
  </ul>
</nav>
