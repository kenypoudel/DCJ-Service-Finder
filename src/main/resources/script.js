let currentPage = 0;
const pageSize = 10;


/*
 * Load services from the backend
 */
async function loadServices(page = 0) {

  currentPage = page;

  const keyword =
    document.getElementById('search-input').value.trim();


  /*
   * Get selected categories
   */
  const selectedCategories =
    document.querySelectorAll(
      'input[name="filters-instant-categories"]:checked'
    );


  const categories =
    Array.from(selectedCategories)
      .map(checkbox => checkbox.value);


  const category =
    categories.join(',');


  /*
   * Build API URL
   */
  const url =
    `/api/services?keyword=${encodeURIComponent(keyword)}` +
    `&category=${encodeURIComponent(category)}` +
    `&page=${page}` +
    `&size=${pageSize}`;


  try {

    const response = await fetch(url);


    if (!response.ok) {
      throw new Error('Failed to load services');
    }


    const data = await response.json();
    alert("Test" + page);
alert("Data: " + JSON.stringify(data)); // Debugging line to check the data received from the API

    /*
     * Render results
     */
    renderResults(data);


    /*
     * Render pagination
     */
    renderPagination(data);


  } catch (error) {

    console.error(error);

    document.getElementById('search-results').innerHTML =
      '<p>Unable to load services. Please try again.</p>';

    document.getElementById('pagination-container').innerHTML = '';

  }
}

async function loadCategories() {
  const categoryList = document.getElementById('category-list');

  try {
    const response = await fetch('/api/categories');
    if (!response.ok) {
      throw new Error('Failed to load categories');
    }

    const categories = await response.json();
    categoryList.replaceChildren();

    categories.forEach((category, index) => {
      const input = document.createElement('input');
      input.className = 'nsw-form__checkbox-input';
      input.type = 'checkbox';
      input.name = 'filters-instant-categories';
      input.value = category;
      input.id = `category-${index}`;
      input.addEventListener('change', () => loadServices(0));

      const label = document.createElement('label');
      label.className = 'nsw-form__checkbox-label';
      label.htmlFor = input.id;
      label.textContent = category;

      categoryList.append(input, label);
    });
  } catch (error) {
    console.error(error);
    categoryList.textContent = 'Unable to load categories.';
  }
}


    /*
    * Display service results
    */

    function renderResults(data) {
    const resultsContainer =
        document.getElementById('search-results');

    resultsContainer.innerHTML = '';

    updateResultsInfo(data);

    if (data.content.length === 0) {
        resultsContainer.innerHTML =
        '<p>No services found.</p>';
        return;
    }

    data.content.forEach(service => {

        const result =
        document.createElement('div');

    result.className =
      'nsw-list-item';


    const content =
      document.createElement('div');

    content.className =
      'nsw-list-item__content';


    /*
     * Category
     */
    const category =
      document.createElement('div');

    category.className =
      'nsw-list-item__label';

    category.textContent =
      service.category ?? '';

    content.appendChild(category);


    /*
     * Date
     */
    const date =
      document.createElement('div');

    date.className =
      'nsw-list-item__info';

    date.textContent =
      service.date ?? '';

    content.appendChild(date);


    /*
     * Title and link
     */
    const title =
      document.createElement('div');

    title.className =
      'nsw-list-item__title';

    const link =
      document.createElement('a');

    if (service.url && service.url.trim()) {
      link.href = service.url;
      link.target = '_blank';
      link.rel = 'noopener noreferrer';
      link.textContent = service.title ?? '';
      title.appendChild(link);
    } else {
      title.textContent = service.title ?? '';
    }

    content.appendChild(title);


    /*
     * Description
     */
    const description =
      document.createElement('div');

    description.className =
      'nsw-list-item__copy';

    description.textContent =
      service.description ?? '';

    content.appendChild(description);


    /*
     * Add the completed result to the page.
     */
    result.appendChild(content);

    resultsContainer.appendChild(result);
  });
}




/*
 * Display:
 *
 * Showing results 1 - 10 of 50 results
 */
function updateResultsInfo(data) {

  const resultsInfo =
    document.getElementById('results-info');


  if (data.totalResults === 0) {

    resultsInfo.textContent =
      'Showing 0 results';

    return;
  }


  const firstResult =
    data.page * data.size + 1;


  const lastResult =
    Math.min(
      (data.page + 1) * data.size,
      data.totalResults
    );


  resultsInfo.textContent =
    `Showing results ${firstResult} - ${lastResult} of ${data.totalResults} results`;
}


/*
 * Create pagination
 */
function renderPagination(data) {

  const container =
    document.getElementById('pagination-container');


  container.innerHTML = '';


  /*
   * Don't show pagination if there is only one page
   */
  if (data.totalPages <= 1) {
    return;
  }


  const pagination =
    document.createElement('nav');

  pagination.className =
    'nsw-pagination';

  pagination.setAttribute(
    'aria-label',
    'Pagination'
  );


  const list =
    document.createElement('ul');


  /*
   * Previous button
   */
  const previousItem =
    document.createElement('li');


  const previousLink =
    document.createElement('a');

  previousLink.className =
    'nsw-icon-button';


  previousLink.href = '#';


  previousLink.setAttribute(
    'aria-label',
    'Previous page'
  );


  if (data.page === 0) {

    previousLink.classList.add(
      'pagination-disabled'
    );

  } else {

    previousLink.addEventListener(
      'click',
      function(event) {

        event.preventDefault();

        loadServices(data.page - 1);

      }
    );

  }


  previousLink.innerHTML =
    '<span class="material-icons nsw-material-icons">' +
    'keyboard_arrow_left' +
    '</span>';


  previousItem.appendChild(previousLink);

  list.appendChild(previousItem);


  /*
   * Page numbers
   */
  for (
    let page = 0;
    page < data.totalPages;
    page++
  ) {

    const pageItem =
      document.createElement('li');


    const pageLink =
      document.createElement('a');


    pageLink.href = '#';


    pageLink.textContent =
      page + 1;


    /*
     * Current page
     */
    if (page === data.page) {

      pageLink.setAttribute(
        'aria-current',
        'page'
      );

    }


    pageLink.addEventListener(
      'click',
      function(event) {

        event.preventDefault();

        loadServices(page);

      }
    );


    pageItem.appendChild(pageLink);

    list.appendChild(pageItem);

  }


  /*
   * Next button
   */
  const nextItem =
    document.createElement('li');


  const nextLink =
    document.createElement('a');


  nextLink.className =
    'nsw-icon-button';


  nextLink.href = '#';


  nextLink.setAttribute(
    'aria-label',
    'Next page'
  );


  if (data.page >= data.totalPages - 1) {

    nextLink.classList.add(
      'pagination-disabled'
    );

  } else {

    nextLink.addEventListener(
      'click',
      function(event) {

        event.preventDefault();

        loadServices(data.page + 1);

      }
    );

  }


  nextLink.innerHTML =
    '<span class="material-icons nsw-material-icons">' +
    'keyboard_arrow_right' +
    '</span>';


  nextItem.appendChild(nextLink);

  list.appendChild(nextItem);


  pagination.appendChild(list);

  container.appendChild(pagination);
}


/*
 * Search button
 */
document
  .getElementById('search-button')
  .addEventListener(
    'click',
    function() {

      loadServices(0);

    }
  );


/*
 * Clear filters
 */
document
  .getElementById('clear-filters')
  .addEventListener(
    'click',
    function(event) {

      event.preventDefault();


      document
        .querySelectorAll(
          'input[name="filters-instant-categories"]'
        )
        .forEach(checkbox => {

          checkbox.checked = false;

        });


      loadServices(0);

    }
  );


/*
 * Initial page load
 */
loadCategories().then(() => loadServices(0));