/*
 * Current page number.
 *
 * The application uses zero-based page numbering:
 * 0 = first page
 * 1 = second page
 * 2 = third page
 */
let currentPage = 0;

/*
 * Maximum number of services displayed on each page.
 */
const pageSize = 10;

/*
 * Load services from the backend.
 *
 * This function:
 * 1. Reads the search keyword.
 * 2. Reads the selected categories.
 * 3. Builds the API request URL.
 * 4. Calls the backend API.
 * 5. Displays the returned results.
 * 6. Displays pagination.
 *
 * The page parameter determines which page of results to request.
 */
async function loadServices(page = 0) {
  currentPage = page;

  const keyword = document.getElementById("search-input").value.trim();

  const selectedCategories = document.querySelectorAll(
    'input[name="filters-instant-categories"]:checked',
  );

  const categories = Array.from(selectedCategories).map(
    (checkbox) => checkbox.value,
  );

  const category = categories.join(",");

  const url =
    `/api/services?keyword=${encodeURIComponent(keyword)}` +
    `&category=${encodeURIComponent(category)}` +
    `&page=${page}` +
    `&size=${pageSize}`;

  try {
    const response = await fetch(url);

    if (!response.ok) {
      throw new Error("Failed to load services");
    }

    const data = await response.json();

    renderResults(data);

    renderPagination(data);
  } catch (error) {
    console.error(error);

    document.getElementById("search-results").innerHTML =
      "<p>Unable to load services. Please try again.";

    document.getElementById("pagination-container").innerHTML = "";
  }
}

/*
 * Load categories from the backend.
 *
 * The categories are generated dynamically rather than
 * being hardcoded in the HTML.
 */
async function loadCategories() {
  const categoryList = document.getElementById("category-list");

  try {
    const response = await fetch("/api/categories");

    if (!response.ok) {
      throw new Error("Failed to load categories");
    }

    const categories = await response.json();

    categoryList.replaceChildren(); // Clear any existing content before adding new checkboxes.

    categories.forEach((category, index) => {
      //Create a checkbox and label for each category.

      const input = document.createElement("input");

      input.className = "nsw-form__checkbox-input";

      input.type = "checkbox";

      input.name = "filters-instant-categories";

      input.value = category;

      input.id = `category-${index}`;

      input.addEventListener("change", () => loadServices(0)); // Reload services starting from the first page when a category is selected or deselected.

      const label = document.createElement("label");

      label.className = "nsw-form__checkbox-label";

      label.htmlFor = input.id;

      label.textContent = category;

      categoryList.append(input, label);
    });
  } catch (error) {
    console.error(error);

    categoryList.textContent = "Unable to load categories.";
  }
}

/*
 * Display service results.
 *
 * The data parameter contains the response returned
 * from the backend API.
 */
function renderResults(data) {
  const resultsContainer = document.getElementById("search-results");

  resultsContainer.innerHTML = "";

  updateResultsInfo(data);

  if (data.content.length === 0) {
    resultsContainer.innerHTML = "<p>No services found.</p>";

    return;
  }

  data.content.forEach((service) => {
    const result = document.createElement("div");

    result.className = "nsw-list-item";

    const content = document.createElement("div");

    content.className = "nsw-list-item__content";

    const category = document.createElement("div");

    category.className = "nsw-list-item__label";

    category.textContent = service.category ?? "";

    content.appendChild(category);

    const date = document.createElement("div");

    date.className = "nsw-list-item__info";

    date.textContent = service.date ?? "";

    content.appendChild(date);

    const title = document.createElement("div");

    title.className = "nsw-list-item__title";

    const link = document.createElement("a");

    if (service.url && service.url.trim()) {
      link.href = service.url;

      link.target = "_blank";

      link.rel = "noopener noreferrer"; // Security best practice for external links.

      link.textContent = service.title ?? "";

      title.appendChild(link);
    } else {
      title.textContent = service.title ?? "";
    }

    content.appendChild(title);

    const description = document.createElement("div");

    description.className = "nsw-list-item__copy";

    description.textContent = service.description ?? "";

    content.appendChild(description);

    result.appendChild(content);

    resultsContainer.appendChild(result);
  });
}

/*
 * Display the result count.
 *
 * Example:
 * Showing results 1 - 10 of 50 results
 */
function updateResultsInfo(data) {
  const resultsInfo = document.getElementById("results-info");

  if (data.totalResults === 0) {
    resultsInfo.textContent = "Showing 0 results";

    return;
  }

  const firstResult = data.page * data.size + 1;

  const lastResult = Math.min((data.page + 1) * data.size, data.totalResults);

  resultsInfo.textContent = `Showing results ${firstResult} - ${lastResult} of ${data.totalResults} results`;
}

/*
 * Create pagination controls.
 *
 * This function creates:
 * - Previous button
 * - Page number buttons
 * - Next button
 */
function renderPagination(data) {
  const container = document.getElementById("pagination-container");

  container.innerHTML = "";

  if (data.totalPages <= 1) {
    return;
  }

  const pagination = document.createElement("nav");

  pagination.className = "nsw-pagination";

  pagination.setAttribute("aria-label", "Pagination");

  const list = document.createElement("ul");

  const previousItem = document.createElement("li");

  const previousLink = document.createElement("a");

  previousLink.className = "nsw-icon-button";

  previousLink.href = "#";

  previousLink.setAttribute("aria-label", "Previous page");

  if (data.page === 0) {
    previousLink.classList.add("pagination-disabled");
  } else {
    previousLink.addEventListener("click", function (event) {
      event.preventDefault();

      loadServices(data.page - 1);
    });
  }

  previousLink.innerHTML =
    '<span class="material-icons nsw-material-icons">' +
    "keyboard_arrow_left" +
    "</span>";

  previousItem.appendChild(previousLink);

  list.appendChild(previousItem);

  for (let page = 0; page < data.totalPages; page++) {
    const pageItem = document.createElement("li");

    const pageLink = document.createElement("a");

    pageLink.href = "#";

    pageLink.textContent = page + 1;

    if (page === data.page) {
      pageLink.setAttribute("aria-current", "page");
    }

    pageLink.addEventListener("click", function (event) {
      event.preventDefault();

      loadServices(page);
    });

    pageItem.appendChild(pageLink);

    list.appendChild(pageItem);
  }

  const nextItem = document.createElement("li");

  const nextLink = document.createElement("a");

  nextLink.className = "nsw-icon-button";

  nextLink.href = "#";

  nextLink.setAttribute("aria-label", "Next page");

  if (data.page >= data.totalPages - 1) {
    nextLink.classList.add("pagination-disabled");
  } else {
    nextLink.addEventListener("click", function (event) {
      event.preventDefault();

      loadServices(data.page + 1);
    });
  }

  nextLink.innerHTML =
    '<span class="material-icons nsw-material-icons">' +
    "keyboard_arrow_right" +
    "</span>";

  nextItem.appendChild(nextLink);

  list.appendChild(nextItem);

  pagination.appendChild(list);

  container.appendChild(pagination);
}

/*
 * -------------------------
 * Search button
 * -------------------------
 *
 * When the user clicks Search:
 * - Start from the first page.
 * - Load services using the current keyword
 *   and selected categories.
 */
document.getElementById("search-button").addEventListener("click", function () {
  loadServices(0);
});

/*
 * -------------------------
 * Clear filters
 * -------------------------
 *
 * When the user clicks Clear:
 * - Uncheck all category checkboxes.
 * - Load the first page again.
 */
document
  .getElementById("clear-filters")
  .addEventListener("click", function (event) {
    /*
     * Prevent the default action of the link/button.
     */
    event.preventDefault();

    document
      .querySelectorAll('input[name="filters-instant-categories"]')
      .forEach((checkbox) => {
        checkbox.checked = false;
      });

    loadServices(0);
  });

/*
 * -------------------------
 * Initial page load
 * -------------------------
 *
 * First load the categories.
 *
 * Once the category request has completed successfully,
 * load the initial service results.
 *
 * This ensures the category checkboxes are available
 * before the service search is displayed.
 */
loadCategories().then(() => loadServices(0));
