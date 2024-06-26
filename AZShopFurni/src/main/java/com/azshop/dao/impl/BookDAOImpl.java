package com.azshop.dao.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;

import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.filter.*;

import com.azshop.dao.IBookDAO;
import com.azshop.models.BookModel;

public class BookDAOImpl implements IBookDAO {
	private static final byte[] INFO_CF = Bytes.toBytes("info");
	private static final byte[] DETAIL_CF = Bytes.toBytes("detail");

	@Override
	public List<BookModel> findAll() throws IOException {
		List<BookModel> books = new ArrayList<>();
		Configuration conf = new Configuration();

		try (Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf("books"))) {

			Scan scan = new Scan();
			scan.addFamily(INFO_CF);
			scan.addFamily(DETAIL_CF);

			try (ResultScanner scanner = table.getScanner(scan)) {
				for (Result result : scanner) {
					BookModel book = constructBookFromResult(result);
					books.add(book);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return books;
	}

	@Override
	public BookModel findOne(String id) throws IOException {
		Configuration conf = new Configuration();
		Connection connection = ConnectionFactory.createConnection(conf);
		Table table = connection.getTable(TableName.valueOf("books"));

		Get get = new Get(Bytes.toBytes(id));
		BookModel bookmodel = null;
		try {
			Result result = table.get(get);
			if (!result.isEmpty()) {
				bookmodel = constructBookFromResult(result);
			} else {
				System.out.println("Empty");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bookmodel;
	}

	@Override
	public List<BookModel> findByName(String filterValue) throws IOException {
		String tableName = "books";
		String columnFamily = "info";
		String columnName = "title";
		List<BookModel> books = new ArrayList<>();

		Configuration conf = new Configuration();
		try (Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf(tableName))) {
			Scan scan = new Scan();
			SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(columnFamily),
					Bytes.toBytes(columnName), CompareOperator.EQUAL, new BinaryComparator(filterValue.getBytes()));
			scan.setFilter(filter);

			try (ResultScanner scanner = table.getScanner(scan)) {
				for (Result result : scanner) {
					BookModel book = constructBookFromResult(result);
					books.add(book);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return books;
	}

	@Override
	public List<BookModel> findByCategory(String cate) throws IOException {
		String tableName = "books";
		String columnFamily = "info";
		String columnName = "categories";
		List<BookModel> books = new ArrayList<>();

		Configuration conf = new Configuration();
		try (Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf(tableName))) {
			Scan scan = new Scan();
			scan.setLimit(20);

			SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(columnFamily),
					Bytes.toBytes(columnName), CompareOperator.EQUAL, new BinaryComparator(cate.getBytes()));
			scan.setFilter(filter);

			try (ResultScanner scanner = table.getScanner(scan)) {
				for (Result result : scanner) {
					BookModel book = constructBookFromResult(result);
					books.add(book);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return books;
	}

	@Override
	public List<BookModel> findByAuthor(String filterValue) throws IOException {
		String tableName = "books";
		String columnFamily = "info";
		String columnName = "authors";
		List<BookModel> books = new ArrayList<>();

		Configuration conf = new Configuration();
		try (Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf(tableName))) {
			Scan scan = new Scan();
			SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(columnFamily),
					Bytes.toBytes(columnName), CompareOperator.EQUAL, new BinaryComparator(filterValue.getBytes()));
			scan.setFilter(filter);

			try (ResultScanner scanner = table.getScanner(scan)) {
				for (Result result : scanner) {
					BookModel book = constructBookFromResult(result);
					books.add(book);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return books;
	}

	public List<BookModel> findWithCount(int count) {
		List<BookModel> books = new ArrayList<>();
		Configuration conf = new Configuration();

		try (Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf("books"))) {

			Scan scan = new Scan();
			scan.addFamily(INFO_CF);
			scan.addFamily(DETAIL_CF);
			scan.setLimit(count);

			try (ResultScanner scanner = table.getScanner(scan)) {
				for (Result result : scanner) {
					BookModel book = constructBookFromResult(result);
					books.add(book);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return books;
	}

	@Override
	public void insert(BookModel model) {
		try {
			if (model != null) {

				Configuration conf = new Configuration();
				Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf("books"));

				Put put = new Put(Bytes.toBytes(model.getIsbn10()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("isbn13"), Bytes.toBytes(model.getIsbn13()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("isbn10"), Bytes.toBytes(model.getIsbn10()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("title"), Bytes.toBytes(model.getTitle()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("authors"), Bytes.toBytes(model.getAuthors()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("categories"), Bytes.toBytes(model.getCategories()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("thumbnail"), Bytes.toBytes(model.getThumbnail()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("description"),
						Bytes.toBytes(model.getDescription()));
				put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("published_year"),
						Bytes.toBytes(model.getPublished_year()));
				put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("average_rating"),
						Bytes.toBytes(model.getAverage_rating()));
				put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("numbers"), Bytes.toBytes(model.getNum_pages()));
				put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("ratings_count"),
						Bytes.toBytes(model.getRatings_count()));

				table.put(put);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void delete(String id) {
		try {
			Configuration conf = new Configuration();
			Connection connection = ConnectionFactory.createConnection(conf);
			Table table = connection.getTable(TableName.valueOf("books"));
			Delete delete = new Delete(id.getBytes());
			table.delete(delete);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void update(BookModel model) {
		try {
			if (model != null) {

				Configuration conf = new Configuration();
				Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf("books"));
				Put put = new Put(Bytes.toBytes(model.getIsbn10()));

				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("isbn10"), Bytes.toBytes(model.getIsbn10()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("title"), Bytes.toBytes(model.getTitle()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("authors"), Bytes.toBytes(model.getAuthors()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("categories"), Bytes.toBytes(model.getCategories()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("thumbnail"), Bytes.toBytes(model.getThumbnail()));
				put.addColumn(Bytes.toBytes("info"), Bytes.toBytes("description"),
						Bytes.toBytes(model.getDescription()));
				put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("published_year"),
						Bytes.toBytes(model.getPublished_year()));
				put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("average_rating"),
						Bytes.toBytes(model.getAverage_rating()));
				put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("numbers"), Bytes.toBytes(model.getNum_pages()));
				put.addColumn(Bytes.toBytes("detail"), Bytes.toBytes("ratings_count"),
						Bytes.toBytes(model.getRatings_count()));

				table.put(put);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<BookModel> filterByRating(int desiredRating) throws IOException {
		String tableName = "books";
		String columnFamily = "detail";
		String columnName = "average_rating";
		String rating = String.valueOf(desiredRating);

		List<BookModel> books = new ArrayList<>();

		Configuration conf = new Configuration();
		try (Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf(tableName))) {
			Scan scan = new Scan();
			SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(columnFamily),
					Bytes.toBytes(columnName), CompareOperator.GREATER_OR_EQUAL,
					new BinaryComparator(Bytes.toBytes(Float.parseFloat(rating))));

			scan.setFilter(filter);

			try (ResultScanner scanner = table.getScanner(scan)) {
				for (Result result : scanner) {
					BookModel book = constructBookFromResult(result);
					books.add(book);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return books;
	}

	@Override
	public List<String> findAuthor() throws IOException {
		Configuration conf = new Configuration();
		Connection connection = ConnectionFactory.createConnection(conf);
		Table table = connection.getTable(TableName.valueOf("books"));
		List<String> listAuthor = new ArrayList<>();
		try {
			Scan scan = new Scan();
			scan.addFamily(INFO_CF);
			ResultScanner scanner = table.getScanner(scan);
			for (Result result : scanner) {
				listAuthor.add(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("authors"))));
			}
		} finally {
			table.close();
			connection.close();
		}
		return listAuthor;
	}

	@Override
	public List<BookModel> findSameCategory(String categories) {
		String tableName = "books";
		String columnFamily = "info";
		String columnName = "categories";
		List<BookModel> books = new ArrayList<>();

		Configuration conf = new Configuration();
		try (Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf(tableName))) {
			Scan scan = new Scan();
			SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(columnFamily),
					Bytes.toBytes(columnName), CompareOperator.EQUAL, new BinaryComparator(Bytes.toBytes(categories)));
			scan.setFilter(filter);

			try (ResultScanner scanner = table.getScanner(scan)) {
				for (Result result : scanner) {
					BookModel book = constructBookFromResult(result);
					books.add(book);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return books;
	}

	@Override
	public List<List<Entry<String, Long>>> findToFilter() {
		String tableName = "books";
		String columnFamily = "info";
		String columnName1 = "categories";
		String columnName2 = "authors";
		List<String> categoriesList = new ArrayList<String>();
		List<String> authorsList = new ArrayList<String>();
		Configuration conf = new Configuration();
		try (Connection connection = ConnectionFactory.createConnection(conf);
				Table table = connection.getTable(TableName.valueOf(tableName))) {
			Scan scan = new Scan();
			try (ResultScanner scanner = table.getScanner(scan)) {
				for (Result result : scanner) {
					byte[] categoryBytes = result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName1));
					if (categoryBytes != null) {
						String category = Bytes.toString(categoryBytes);
						categoriesList.add(category);
					}
					byte[] authorsBytes = result.getValue(Bytes.toBytes(columnFamily), Bytes.toBytes(columnName2));
					if (authorsBytes != null) {
						String authors = Bytes.toString(authorsBytes);
						authorsList.add(authors);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<List<Entry<String, Long>>> listFilter = new ArrayList<List<Entry<String, Long>>>();
		listFilter.add(categoriesList.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting())).entrySet().stream()
				.sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(5).collect(Collectors.toList()));

		listFilter.add(authorsList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.entrySet().stream().sorted(Map.Entry.<String, Long>comparingByValue().reversed()).limit(5)
				.collect(Collectors.toList()));

		return listFilter;
	}

	private BookModel constructBookFromResult(Result result) {
		BookModel bookmodel = new BookModel();

		bookmodel = new BookModel();
		bookmodel.setIsbn13(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("isbn13"))));
		bookmodel.setIsbn10(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("isbn10"))));
		bookmodel.setTitle(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("title"))));
		bookmodel.setAuthors(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("authors"))));
		bookmodel.setCategories(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("categories"))));
		bookmodel.setThumbnail(Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("thumbnail"))));
		String description = Bytes.toString(result.getValue(INFO_CF, Bytes.toBytes("description")));

		// handle description too long
		if (description != null) {
			if (description.length() > 200) {
				if (description.startsWith("\"")) {
					description = description.substring(1, 201);
				} else {
					description = description.substring(0, 200);
				}
			} else if (description.length() <= 200) {
				if (description.startsWith("\"")) {
					description = description.substring(1);
				} else {
					description = description.substring(0);
				}

			}
		}

		bookmodel.setDescription(description);
		byte[] publishedYearBytes = result.getValue(DETAIL_CF, Bytes.toBytes("published_year"));
		if (publishedYearBytes != null && publishedYearBytes.length >= Bytes.SIZEOF_INT) {
			bookmodel.setPublished_year(Bytes.toInt(publishedYearBytes));
		} else {
			bookmodel.setPublished_year(0);
		}

		byte[] averageRatingBytes = result.getValue(DETAIL_CF, Bytes.toBytes("average_rating"));
		if (averageRatingBytes != null && averageRatingBytes.length >= Bytes.SIZEOF_FLOAT) {
			bookmodel.setAverage_rating(Bytes.toFloat(averageRatingBytes));
		} else {
			bookmodel.setAverage_rating(0.0f);
		}
		byte[] ratingsCountBytes = result.getValue(DETAIL_CF, Bytes.toBytes("ratings_count"));
		if (ratingsCountBytes != null && ratingsCountBytes.length >= Bytes.SIZEOF_INT) {
			bookmodel.setRatings_count(Bytes.toInt(ratingsCountBytes));
		} else {
			bookmodel.setRatings_count(0);
		}
		byte[] numPages = result.getValue(DETAIL_CF, Bytes.toBytes("numbers"));
		if (numPages != null && numPages.length >= Bytes.SIZEOF_INT) {
			bookmodel.setNum_pages(Bytes.toInt(numPages));
		} else {
			bookmodel.setNum_pages(0);
		}
		return bookmodel;
	}
}
